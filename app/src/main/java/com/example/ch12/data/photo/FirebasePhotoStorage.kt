package com.example.ch12.data.photo

import com.example.ch12.data.auth.AuthRepository
import com.example.ch12.data.note.NotSignedInException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Struktur berkas:  users/{uid}/profile.jpg  (sejajar dengan path Firestore)
@Singleton
@Suppress("unused")
class FirebasePhotoStorage @Inject constructor(
    private val storage: FirebaseStorage,
    private val authRepository: AuthRepository
) : PhotoStorage {

    // TODO [T4.3] Ubah UploadTask (callback) menjadi Flow, seperti snapshot listener di Ch11:
    //   callbackFlow {
    //     val uid = authRepository.currentUser?.uid ?: throw NotSignedInException()
    //     val ref = storage.reference.child("users/$uid/profile.jpg")     // harus sama dengan storage.rules
    //     val metadata = StorageMetadata.Builder().setContentType("image/jpeg").build()
    //     val task = ref.putBytes(bytes, metadata)
    //       .addOnProgressListener { s -> trySend(UploadEvent.Progress(s.bytesTransferred, s.totalByteCount)) }
    //       .addOnSuccessListener  { launch { send(Done(ref.downloadUrl.await().toString())); close() } }
    //       .addOnFailureListener  { e -> close(PhotoUploadException(storageErrorMessage(e), e)) }
    //     awaitClose { if (!task.isComplete) task.cancel() }    // batal = upload dihentikan
    //   }
    //   Bungkus send(Done) dengan try/catch: downloadUrl juga bisa gagal.
    override fun uploadProfilePhoto(bytes: ByteArray): Flow<UploadEvent> = callbackFlow {
        val uid = authRepository.currentUser?.uid ?: throw NotSignedInException()
        val ref = storage.reference.child("users/$uid/profile.jpg")
        val metadata = StorageMetadata.Builder().setContentType("image/jpeg").build()
        val task = ref.putBytes(bytes, metadata)
            .addOnProgressListener { snapshot ->
                trySend(UploadEvent.Progress(snapshot.bytesTransferred, snapshot.totalByteCount))
            }
            .addOnSuccessListener {
                launch {
                    try {
                        send(UploadEvent.Done(ref.downloadUrl.await().toString()))
                        close()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        close(PhotoUploadException(storageErrorMessage(e), e))
                    }
                }
            }
            .addOnFailureListener { e ->
                close(PhotoUploadException(storageErrorMessage(e), e))
            }
        awaitClose { if (!task.isComplete) task.cancel() }
    }
}

@Suppress("unused")
private fun storageErrorMessage(e: Exception): String =
    if (e is StorageException) storageErrorMessageForCode(e.errorCode)
    else e.message ?: "Upload gagal. Coba lagi."
