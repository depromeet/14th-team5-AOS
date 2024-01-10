package com.no5ing.bbibbi.presentation.ui.feature.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.state.main.home.HomePageContentState
import com.no5ing.bbibbi.presentation.state.main.home.HomePageState
import com.no5ing.bbibbi.presentation.state.main.home.HomePageStoryBarState
import com.no5ing.bbibbi.presentation.state.main.home.rememberHomePageContentState
import com.no5ing.bbibbi.presentation.state.main.home.rememberHomePageState
import com.no5ing.bbibbi.presentation.state.main.home.rememberHomePageStoryBarState
import com.no5ing.bbibbi.presentation.ui.theme.BbibbiTheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.util.BackToExitHandler
import com.no5ing.bbibbi.presentation.viewmodel.auth.RetrieveMeViewModel
import com.no5ing.bbibbi.presentation.viewmodel.members.FamilyMembersViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.IsMeUploadedTodayViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.MainPostFeedViewModel
import com.no5ing.bbibbi.util.LocalSessionState
import timber.log.Timber

@Composable
fun HomePage(
    homePageState: HomePageState = rememberHomePageState(),
    retrieveMeViewModel: RetrieveMeViewModel = hiltViewModel(),
    isMeUploadedTodayViewModel: IsMeUploadedTodayViewModel = hiltViewModel(),
    familyPostsViewModel: MainPostFeedViewModel = hiltViewModel(),
    familyMembersViewModel: FamilyMembersViewModel = hiltViewModel(),
    homePageContentState: HomePageContentState = rememberHomePageContentState(
        uiState = familyPostsViewModel.uiState
    ),
    storyBarState: HomePageStoryBarState = rememberHomePageStoryBarState(
        uiState = familyMembersViewModel.uiState,
        meState = retrieveMeViewModel.uiState,
    ),
    onTapLeft: () -> Unit = {},
    onTapRight: () -> Unit = {},
    onTapProfile: (Member) -> Unit = {},
    onTapContent: (Post) -> Unit = {},
    onTapUpload: () -> Unit = {},
    onTapInvite: () -> Unit = {},
) {
    val memberId = LocalSessionState.current.memberId
    val meUploadedState = isMeUploadedTodayViewModel.uiState.collectAsState()
    //val familyMembersState = familyMembersViewModel.uiState.collectAsState()
    val meState = retrieveMeViewModel.uiState.collectAsState()
    BackToExitHandler()
    LaunchedEffect(Unit) {
        isMeUploadedTodayViewModel.invoke(Arguments(arguments = mapOf("memberId" to memberId)))
    }
    LaunchedEffect(meState.value.status) {
        if(meState.value.isIdle()) {
            retrieveMeViewModel.invoke(Arguments())
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(MaterialTheme.bbibbiScheme.backgroundPrimary)
        ) {
            HomePageTopBar(
                onTapLeft = onTapLeft,
                onTapRight = onTapRight
            )
            HomePageContent(
                familyMembersViewModel = familyMembersViewModel,
                familyPostsViewModel = familyPostsViewModel,
                homePageContentState = homePageContentState,
                retrieveMeViewModel = retrieveMeViewModel,
                storyBarState = storyBarState,
                onTapContent = onTapContent,
                onTapProfile = onTapProfile,
                onTapInvite = onTapInvite,
            )
        }
        if (meUploadedState.value.isReady() && !meUploadedState.value.data) {
            val gap = gapUntilNext()
            if (gap > 0) {
                HomePageUploadButton(
                    onTap = onTapUpload
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "Preview",
    showSystemUi = true
)
@Composable
fun HomePagePreview() {
    BbibbiTheme {
        HomePage(
            retrieveMeViewModel = hiltViewModel(),
            isMeUploadedTodayViewModel = hiltViewModel(),
        )
    }
}

