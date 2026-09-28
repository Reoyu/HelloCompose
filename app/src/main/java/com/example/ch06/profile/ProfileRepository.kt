package com.example.ch06.profile

data class UserProfile(
    val username: String,
    val notificationsEnabled: Boolean
)

interface ProfileRepository {
    fun getProfile(): UserProfile
}

class FakeProfileRepository : ProfileRepository {

    override fun getProfile(): UserProfile {
        return UserProfile(
            username = "Mahasiswa Android",
            notificationsEnabled = true
        )
    }
}