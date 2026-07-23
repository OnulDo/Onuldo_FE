package com.example.onuldo_fe.ui.screen.party

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.party.PartyChallengeGalleryCard
import com.example.onuldo_fe.viewmodel.party.PartyChallengeCardUi
import com.example.onuldo_fe.viewmodel.party.PartyChallengeUi
import com.example.onuldo_fe.ui.theme.*

@Composable
fun PartyChallengeSelectScreen(
    challenges: List<PartyChallengeCardUi>,
    onSelect: (PartyChallengeUi) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit = {},
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    val searchInteractionSource = remember { MutableInteractionSource() }
    val isSearchFocused by searchInteractionSource.collectIsFocusedAsState()
    val filterInteractionSource = remember { MutableInteractionSource() }
    val isFilterPressed by filterInteractionSource.collectIsPressedAsState()
    val categories = challenges.map { it.challenge.category }.distinct()
    val items = challenges.filter {
        (searchText.isBlank() || it.challenge.title.contains(searchText, ignoreCase = true)) &&
            (selectedCategory == null || it.challenge.category == selectedCategory)
    }

    // 탐색을 취소하면 Route에서 임시 선택값을 제거하고 파티 만들기 화면으로 돌아감
    BackHandler(onBack = onBack)

    Column(Modifier.fillMaxSize().background(SourCream).systemBarsPadding()) {
        Text("챌린지", Modifier.padding(start = 20.dp, top = 23.dp), color = BlackBrown, fontFamily = Pretendard, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("나에게 맞는 챌린지를 찾아보세요", Modifier.padding(start = 20.dp, top = 2.dp), color = BlackBrown.copy(alpha = 0.7f), fontFamily = Pretendard, fontSize = 13.sp, fontWeight = FontWeight.Normal)

        Row(Modifier.padding(start = 20.dp, end = 26.dp, top = 17.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(27.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(White)
                    .border(
                        1.dp,
                        if (isSearchFocused) Persimmon.copy(alpha = 0.4f) else DarkBrown40,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 11.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.party_challenge_search),
                    contentDescription = null,
                    tint = if (isSearchFocused) Persimmon else DarkBrown,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(11.dp))
                Box(Modifier.weight(1f)) {
                    val searchTextStyle = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 13.sp,
                        lineHeight = 13.sp
                    )
                    if (searchText.isEmpty()) {
                        Text("생활루틴", style = searchTextStyle, color = DarkBrown40)
                    }
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        singleLine = true,
                        textStyle = searchTextStyle.copy(color = MaterialTheme.colorScheme.onBackground),
                        cursorBrush = SolidColor(Persimmon),
                        interactionSource = searchInteractionSource,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(Modifier.width(9.dp))
            Box {
                Image(
                    painter = painterResource(
                        if (isFilterPressed || selectedCategory != null) R.drawable.party_challenge_filter_pressed
                        else R.drawable.party_challenge_filter_default
                    ),
                    contentDescription = "필터",
                    modifier = Modifier
                        .size(27.dp)
                        .clickable(
                            interactionSource = filterInteractionSource,
                            indication = null,
                            onClick = { showCategoryMenu = true }
                        )
                )
                DropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("전체") },
                        onClick = {
                            selectedCategory = null
                            showCategoryMenu = false
                        }
                    )
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                showCategoryMenu = false
                            }
                        )
                    }
                }
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Persimmon)
            }
        } else if (errorMessage != null) {
            Column(
                Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(errorMessage, color = DarkBrown, fontFamily = Pretendard, fontSize = 13.sp)
                TextButton(onClick = onRetry) { Text("다시 시도", color = Persimmon) }
            }
        } else LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f).padding(top = 21.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.challenge.id }) { item ->
                PartyChallengeGalleryCard(
                    item = item,
                    onClick = {
                        onSelect(item.challenge)
                        onConfirm()
                    }
                )
            }
        }
    }
}

private val previewPartyChallengeCards = listOf(
    PartyChallengeCardUi(
        challenge = PartyChallengeUi("preview-1", "새벽 6시 기상", "생활루틴"),
        participantCount = 1_234,
        fallbackImageRes = R.drawable.party_challenge_morning
    ),
    PartyChallengeCardUi(
        challenge = PartyChallengeUi("preview-2", "30분 러닝", "피트니스"),
        participantCount = 682,
        fallbackImageRes = R.drawable.party_challenge_running
    )
)

@Preview(name = "파티 챌린지 탐색", showBackground = true, widthDp = 390, heightDp = 884)
@Composable private fun PartyChallengeSelectScreenPreview() {
    OnulDo_FETheme {
        PartyChallengeSelectScreen(
            challenges = previewPartyChallengeCards,
            onSelect = {},
            onConfirm = {},
            onBack = {}
        )
    }
}
