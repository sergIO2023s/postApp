package com.example.postapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.domain.*
import com.example.postapp.domain.utils.Response
import com.example.postapp.util.getOrAwaitValue
import com.example.postapp.viewmodels.PostListViewModel
import com.example.postapp.viewmodels.UIListSectionState
import com.example.postapp.viewmodels.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import org.mockito.Mockito.*
import kotlin.test.assertEquals


class PostListViewModelTest: KoinTest {


    val viewmodel by inject<PostListViewModel>()

    private val getPostsUseCase = mock(GetPostsUseCase::class.java)
    private val setFavoriteUseCase = mock(SetFavoriteUseCase::class.java)
    private val deleteAllUseCase = mock(DeleteAllUseCase::class.java)
    private val getLatestPostUseCase = mock(GetLatestPostUseCase::class.java)
    private val deletePostByIdUseCase = mock(DeletePostById::class.java)

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
                single { PostListViewModel(get(),get(),get(),get(),get()) }
                single { getPostsUseCase }
                single { setFavoriteUseCase }
                single { deleteAllUseCase }
                single { getLatestPostUseCase }
                single { deletePostByIdUseCase }
            }
        )
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun given_MethodHasntBeenBalled_when_Called_then_EmitLoadInstate() = runTest {

        val testFlowForEmitting = MutableSharedFlow<Response<List<Post>>>()

        doAnswer {
            testFlowForEmitting
        }.`when`(getPostsUseCase).execute()

        val postsList = (0..300)
            .map { Post(it.toString(),"1","","", false) }


        viewmodel.getPosts()
        val newState = viewmodel.liveDataUiState.getOrAwaitValue()
        assertEquals(newState.isLoading, true)

    }


    @Test
    fun given_LoadingStateProduced_when_NewDataiSEmitted_then_UIStateUpdatingTheViewIsProduced() = runTest {

        val testFlowForEmitting = MutableSharedFlow<Response<List<Post>>>()

        doAnswer {
            testFlowForEmitting
        }.`when`(getPostsUseCase).execute()

        val postsList = (0..300)
            .map { Post(it.toString(),"1","","", false) }

        val successBusinessResponse = Response.Success(postsList) as Response<List<Post>>

        viewmodel.getPosts()
        val newState = viewmodel.liveDataUiState.getOrAwaitValue()
        assertEquals(newState.isLoading, true)

        testFlowForEmitting.emit(successBusinessResponse)
        var liveDataValue = viewmodel.liveDataUiState.getOrAwaitValue()
        var liveDataListSectionState = viewmodel.liveDataListSectionState.getOrAwaitValue()
        assertEquals(false, liveDataValue.isLoading )
        assertEquals(false, liveDataValue.isShowingError )
        assertEquals(successBusinessResponse.data, liveDataListSectionState.postsList )

    }


    @Test
    fun given_LoadingStateProduced_when_AnErrorInDataLayerIsProduced_then_UIStateShowingTheErrorIsProduced() = runTest {

        val testFlowForEmitting = MutableSharedFlow<Response<List<Post>>>()

        doAnswer {
            testFlowForEmitting
        }.`when`(getPostsUseCase).execute()

        val errorBusinessResponse = Response.Error<Flow<List<Post>>>("Error msg") as Response<List<Post>>

        viewmodel.getPosts()
        val newState = viewmodel.liveDataUiState.getOrAwaitValue()
        assertEquals(newState.isLoading, true)

        var liveDataValue = viewmodel.liveDataUiState.getOrAwaitValue()

        testFlowForEmitting.emit(errorBusinessResponse)
        liveDataValue = viewmodel.liveDataUiState.getOrAwaitValue()
        assertEquals(false, liveDataValue.isLoading )
        assertEquals(true, liveDataValue.isShowingError )

    }

    @Test
    fun given_ErrorIsProduced_when_NewDataiSEmitted_then_UIStateShowingTheErrorIsProduced() = runTest {

        val testFlowForEmitting = MutableSharedFlow<Response<List<Post>>>()

        doAnswer {
            testFlowForEmitting
        }.`when`(getPostsUseCase).execute()

        val errorBusinessResponse = Response.Error<Flow<List<Post>>>("Error msg") as Response<List<Post>>
        val successBusinessResponseWithEmptyList = Response.Success(emptyList<Post>()) as Response<List<Post>>

        viewmodel.getPosts()
        val newState = viewmodel.liveDataUiState.getOrAwaitValue()
        assertEquals(newState.isLoading, true)

        testFlowForEmitting.emit(errorBusinessResponse)
        var liveDataValue = viewmodel.liveDataUiState.getOrAwaitValue()
        assertEquals(false, liveDataValue.isLoading )
        assertEquals(true, liveDataValue.isShowingError )

        testFlowForEmitting.emit(successBusinessResponseWithEmptyList)
        liveDataValue = viewmodel.liveDataUiState.getOrAwaitValue()
        val liveDataListSectionState = viewmodel.liveDataListSectionState.getOrAwaitValue()
        assertEquals(false, liveDataValue.isLoading )
        assertEquals(false, liveDataValue.isShowingError )
        assertEquals(emptyList(), liveDataListSectionState.postsList )
    }

    @Test
    fun given_ViewIsConsumingState_when_UseCaseThrowsAnError_then_EmitErrorInState() = runTest {

        val ERROR_MESSAGE = "ERROR_MESSAGE"

        val testFlowForEmitting = flow<Response<List<Post>>> {
            throw java.lang.Exception(ERROR_MESSAGE)
        }
        doAnswer {
            testFlowForEmitting
        }.`when`(getPostsUseCase).execute()

        viewmodel.getPosts()
        val liveDataValue = viewmodel.liveDataUiState.getOrAwaitValue()
        assertEquals(false, liveDataValue.isLoading )
        assertEquals(true, liveDataValue.isShowingError )
    }





}