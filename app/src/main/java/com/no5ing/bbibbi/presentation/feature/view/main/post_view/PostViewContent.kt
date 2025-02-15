package com.no5ing.bbibbi.presentation.feature.view.main.post_view


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.MiniTextBubbleBox
import com.no5ing.bbibbi.presentation.feature.view.common.AddReactionDialog
import com.no5ing.bbibbi.presentation.feature.view_model.post.AddPostReactionViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.AddRealEmojiViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.MemberRealEmojiListViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.RemovePostReactionViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.asyncImagePainter

@Composable
fun PostViewContent(
    post: Post,
    missionText: String? = null,
    modifier: Modifier = Modifier,
    onTapRealEmojiCreate: (String) -> Unit,
    familyPostReactionBarViewModel: PostReactionBarViewModel = hiltViewModel(),
    removePostReactionViewModel: RemovePostReactionViewModel = hiltViewModel(),
    addPostReactionViewModel: AddPostReactionViewModel = hiltViewModel(),
    addRealEmojiViewModel: AddRealEmojiViewModel = hiltViewModel(),
    postRealEmojiListViewModel: MemberRealEmojiListViewModel = hiltViewModel(),
    addEmojiBarState: MutableState<Boolean> = remember { mutableStateOf(false) },
    postCommentDialogState: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    val memberId = LocalSessionState.current.memberId
    val memberRealEmojiState by postRealEmojiListViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        postRealEmojiListViewModel.invoke(Arguments())
    }
    AddReactionDialog(
        isEnabled = addEmojiBarState,
        //modifier = Modifier.fillMaxWidth(),
        realEmojiMap = memberRealEmojiState,
        onTapEmoji = {
            if (!familyPostReactionBarViewModel.hasEmoji(
                    memberId = memberId,
                    emojiType = it
                )
            ) {
                familyPostReactionBarViewModel.toggleEmoji(
                    memberId = memberId,
                    emojiType = it
                )
                addPostReactionViewModel.invoke(
                    Arguments(
                        resourceId = post.postId,
                        mapOf(
                            "emoji" to it
                        )
                    )
                )
                addEmojiBarState.value = false
            }
        },
        onTapRealEmoji = {
            if (!familyPostReactionBarViewModel.hasRealEmoji(
                    memberId = memberId,
                    realEmojiId = it.realEmojiId
                )
            ) {
                familyPostReactionBarViewModel.reactRealEmoji(
                    memberId = memberId,
                    realEmojiType = it.type,
                    realEmojiId = it.realEmojiId,
                    realEmojiUrl = it.imageUrl
                )
                addRealEmojiViewModel.invoke(
                    Arguments(
                        resourceId = post.postId,
                        mapOf(
                            "realEmojiId" to it.realEmojiId
                        )
                    )
                )
                addEmojiBarState.value = false
            }
        },
        onTapRealEmojiCreate = onTapRealEmojiCreate,
    )
    Column(
        modifier = modifier,
    ) {
        Box {
            Box {
                AsyncImage(
                    model = asyncImagePainter(source = post.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1.0f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(48.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            if (missionText != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(26.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mission_badge),
                            contentDescription = null,
                        )
                        Text(
                            text = missionText,
                            style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                            color = MaterialTheme.bbibbiScheme.white,
                        )
                    }
                }
            }
            MiniTextBubbleBox(
                text = post.content,
                alignment = Alignment.BottomCenter,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Box {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {

                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 13.dp)
                    ) {
                        PostViewReactionBar(
                            modifier = Modifier.weight(1.0f),
                            post = post,
                            isEmojiBarActive = addEmojiBarState.value,
                            onTapAddEmojiButton = {
                                addEmojiBarState.value = !addEmojiBarState.value
                            },
                            familyPostReactionBarViewModel = familyPostReactionBarViewModel,
                            removePostReactionViewModel = removePostReactionViewModel,
                            addPostReactionViewModel = addPostReactionViewModel,
                            postCommentDialogState = postCommentDialogState,
                        )
                    }


                }
            }


        }
    }
}