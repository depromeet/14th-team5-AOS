package com.no5ing.bbibbi.presentation.feature.view_controller.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.data.model.view.MainPageTopBarModel
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarPick
import com.no5ing.bbibbi.presentation.feature.view.main.home.HomePage
import com.no5ing.bbibbi.presentation.feature.view.main.home.TryPickPopup
import com.no5ing.bbibbi.presentation.feature.view_controller.CameraViewPageController.goCameraViewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.NavigationDestination
import com.no5ing.bbibbi.presentation.feature.view_controller.main.CalendarPageController.goCalendarPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.FamilyListPageController.goFamilyListPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostReUploadPageController.goPostReUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostUploadPageController.goPostUploadPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.PostViewPageController.goPostViewPage
import com.no5ing.bbibbi.presentation.feature.view_controller.main.ProfilePageController.goProfilePage
import com.no5ing.bbibbi.presentation.feature.view_model.MainPageViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.members.PickMemberViewModel
import com.no5ing.bbibbi.util.LocalSnackbarHostState

object HomePageController : NavigationDestination(
    route = mainHomePageRoute,
) {
    @Composable
    override fun Render(navController: NavHostController, backStackEntry: NavBackStackEntry) {
        val snackBarHost = LocalSnackbarHostState.current
        val pickMemberViewModel = hiltViewModel<PickMemberViewModel>()
        val mainPageViewModel = hiltViewModel<MainPageViewModel>()
        var isPickDialogVisible by remember { mutableStateOf(false) }
        var tryPickDialogMember by remember { mutableStateOf<MainPageTopBarModel?>(null) }
        val pickState = pickMemberViewModel.uiState.collectAsState()
        LaunchedEffect(pickState.value.status) {
            if (!pickState.value.isIdle()) {
                mainPageViewModel.invoke(Arguments())
            }
        }
        TryPickPopup(
            enabledState = isPickDialogVisible,
            targetNickname = tryPickDialogMember?.displayName ?: "",
            onTapNow = {
                isPickDialogVisible = false
                mainPageViewModel.addPickMembersSet(tryPickDialogMember?.memberId ?: "")
                snackBarHost.showSnackBarWithDismiss(
                    message = "${tryPickDialogMember?.displayName?:""}님에게 생존신고 알림을 보냈어요",
                    actionLabel = snackBarPick,
                )
                pickMemberViewModel.invoke(
                    Arguments(
                        arguments = mapOf(
                            "memberId" to (tryPickDialogMember?.memberId ?: "")
                        )
                    )
                )
            },
            onTapLater = {
                isPickDialogVisible = false
            }
        )
        HomePage(
            onTapLeft = {
                navController.goFamilyListPage()
            },
            onTapRight = {
                navController.goCalendarPage()
            },
            onTapProfile = {
                navController.goProfilePage(it)
            },
            onTapContent = {
                navController.goPostViewPage(it)
            },
            onTapUpload = {
                navController.goPostUploadPage()
                navController.goCameraViewPage()
            },
            onTapInvite = {
                navController.goFamilyListPage()
            },
            onUnsavedPost = {
                navController.goPostReUploadPage(it.toString())
            },
            onTapPick = {
                tryPickDialogMember = it
                isPickDialogVisible = true
            },
            mainPageViewModel = mainPageViewModel,
        )
    }

    fun NavHostController.goHomePage() {
        navigate(route = mainPageRoute)
    }
}
