package com.example.postapp

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.data.datamodels.local.User
import com.example.postapp.data.datamodels.remote.PostFromAPI
import com.example.postapp.data.db.CommentDao
import com.example.postapp.data.db.PostDao
import com.example.postapp.data.db.UserDao
import com.example.postapp.data.remote.JsonPlaceholderApiClient
import com.example.postapp.data.repositories.Repository
import com.example.postapp.domain.utils.Response
import com.example.postapp.postapp.activities.STATE_INITIALIZED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import org.mockito.Mockito.*
import kotlin.test.assertEquals

class PostRepositoryTest: KoinTest {

    val repository by inject<Repository>()

    val postDao = mock(PostDao::class.java)
    val userDao = mock(UserDao::class.java)
    val commentDao = mock(CommentDao::class.java)
    val sharedPrefs = mock(SharedPreferences::class.java)
    val jsonPlaceholderAPIClient = mock(JsonPlaceholderApiClient::class.java)
    val editor = mock(Editor::class.java)



    @ExperimentalCoroutinesApi
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()


    // Run tasks synchronously
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            module {
                single { Repository(get(),get(),get(), get(), get()) }
                single { postDao }
                single { userDao }
                single { commentDao }
                single { sharedPrefs }
                single { jsonPlaceholderAPIClient }
            }
        )
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }


    @Test
    fun given_notanystoragedposts_when_called_then_emitanemptylistAndStoreDataFromServer() = runTest {

        val postsFromAPIList = (0..300)
            .map {  id ->
                PostFromAPI(id.toString(), "","","",false)
            }

        val postEntities = (0..300)
            .map {  id ->
                Post(id.toString(), "","","",false)
            }

        val localStoragedPosts = (0..500)
            .map {  id ->
                PostFromAPI(id.toString(), "","","",false)
            }
        val localStoragedPostsFlow = flowOf(localStoragedPosts)

        doAnswer {
            postsFromAPIList
        }.`when`(jsonPlaceholderAPIClient).getPosts()

        doAnswer {
            flowOf(emptyList<List<Post>>())
        }.`when`(postDao).getAllPosts()

        doAnswer {
            false
        }.`when`(sharedPrefs).getBoolean(STATE_INITIALIZED, false)

        doAnswer {
            editor
        }.`when`(sharedPrefs).edit()

        val testFlow = repository.getPosts()

        testFlow.take(1)

        val values = mutableListOf<Response<List<Post>>>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.getPosts().toList(values)
        }

        assertEquals(Response.Success(emptyList()), values[0])

        verify(postDao).insertPosts(postEntities)


    }
}