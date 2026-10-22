package com.example.ch10.di

import com.example.ch10.data.NewsRepositoryImpl
import com.example.ch10.ui.detail.ArticleDetailViewModel
import com.example.ch10.ui.news.NewsViewModel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import javax.inject.Inject

// Memeriksa "kabel" Hilt tanpa emulator. Sebagian besar anotasi Hilt tidak
// terlihat saat runtime, jadi yang diperiksa adalah class yang DI-GENERATE
// Hilt saat kompilasi (mis. Hilt_MainActivity untuk @AndroidEntryPoint).
// Graf yang tidak lengkap (binding hilang) sudah gagal saat kompilasi.
class HiltSetupTest {

    private fun classExists(name: String) = try {
        Class.forName(name); true
    } catch (e: ClassNotFoundException) {
        false
    }

    private fun hasInjectConstructor(type: Class<*>) =
        type.constructors.any { it.isAnnotationPresent(Inject::class.java) }

    @Test fun `MainApplication memakai @HiltAndroidApp`() = assertTrue(
        classExists("hilt_aggregated_deps._com_example_ch10_MainApplication_GeneratedInjector")
    )

    @Test fun `MainActivity memakai @AndroidEntryPoint`() =
        assertTrue(classExists("com.example.ch10.Hilt_MainActivity"))

    @Test fun `NewsRepositoryImpl punya @Inject constructor`() =
        assertTrue(hasInjectConstructor(NewsRepositoryImpl::class.java))

    @Test fun `kedua ViewModel memakai @HiltViewModel + @Inject constructor`() {
        assertTrue(hasInjectConstructor(NewsViewModel::class.java))
        assertTrue(hasInjectConstructor(ArticleDetailViewModel::class.java))
        assertTrue(classExists("com.example.ch10.ui.news.NewsViewModel_HiltModules"))
        assertTrue(classExists("com.example.ch10.ui.detail.ArticleDetailViewModel_HiltModules"))
    }

    @Test fun `module Network, Database, Repository, dan App terpasang`() {
        listOf("NetworkModule", "DatabaseModule", "RepositoryModule", "AppModule").forEach {
            assertTrue("$it belum ada / belum @InstallIn", classExists("hilt_aggregated_deps._com_example_ch10_di_$it"))
        }
    }

    @Test fun `AppContainer dan DefaultAppContainer sudah dihapus`() {
        assertFalse(classExists("com.example.ch10.di.AppContainer"))
        assertFalse(classExists("com.example.ch10.di.DefaultAppContainer"))
    }

    @Test fun `NewsDatabase tidak lagi punya getInstance (diganti @Singleton)`() {
        val companion = Class.forName("com.example.ch10.data.local.NewsDatabase\$Companion")
        assertFalse(companion.methods.any { it.name == "getInstance" })
    }
}
