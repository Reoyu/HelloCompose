package com.example.ch10

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import com.example.ch10.data.model.NewsFeed
import com.example.ch10.di.FakeNewsRepository
import com.example.ch10.di.fakeArticle
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import javax.inject.Inject

// Uji end-to-end aplikasi SUNGGUHAN (MainActivity, NavHost, hiltViewModel),
// tetapi datanya dari FakeNewsRepository berkat @TestInstallIn.
// Jalankan: ./gradlew connectedDebugAndroidTest (butuh emulator/perangkat)
//
// File ini TIDAK perlu diubah. Ketiga tes baru hijau setelah:
//  - Tugas 1 selesai (MainActivity @AndroidEntryPoint, ViewModel @HiltViewModel), dan
@HiltAndroidTest
class NewsAppHiltTest {

    // HiltAndroidRule harus dijalankan lebih dulu dari rule lain
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createEmptyComposeRule()

    // Instance yang sama dengan yang diterima NewsViewModel (@Singleton)
    @Inject lateinit var repository: FakeNewsRepository

    @Before fun setUp() = hiltRule.inject()

    // Activity dibuka SETELAH jawaban repository diatur
    private fun launchApp() = ActivityScenario.launch(MainActivity::class.java)

    @Test fun daftarBeritaDariFakeRepository() {
        launchApp()

        composeRule.onNodeWithText("Berita Uji 1").assertIsDisplayed()
        composeRule.onNodeWithText("Berita Uji 2").assertIsDisplayed()
    }

    @Test fun ketukArtikelMembukaLayarDetail() {
        launchApp()

        composeRule.onNodeWithText("Berita Uji 1").performClick()

        composeRule.onNodeWithText("Detail Berita").assertIsDisplayed()
        composeRule.onNodeWithText("Oleh Penulis Uji").assertIsDisplayed()
    }

    @Test fun errorLaluCobaLagi() {
        repository.headlines = Result.failure(IOException())
        launchApp()

        composeRule.onNodeWithText("Tidak ada koneksi internet.").assertIsDisplayed()

        repository.headlines = Result.success(NewsFeed(listOf(fakeArticle(3))))
        composeRule.onNodeWithText("Coba Lagi").performClick()

        composeRule.onNodeWithText("Berita Uji 3").assertIsDisplayed()
    }
}
