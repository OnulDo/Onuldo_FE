package com.example.onuldo_fe.ui.screen.verification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onuldo_fe.R
import com.example.onuldo_fe.ui.component.OnulDoBackButton
import com.example.onuldo_fe.ui.component.OnulDoButton
import com.example.onuldo_fe.ui.screen.verification.VerificationStepCard.component.VerificationStepCard
import com.example.onuldo_fe.ui.theme.BlackBrown
import com.example.onuldo_fe.ui.theme.DarkBrown
import com.example.onuldo_fe.ui.theme.DarkBrown40
import com.example.onuldo_fe.ui.theme.DarkBrown70
import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
import com.example.onuldo_fe.ui.theme.Persimmon
import com.example.onuldo_fe.ui.theme.Persimmon10
import com.example.onuldo_fe.ui.theme.Persimmon20
import com.example.onuldo_fe.ui.theme.Red
import com.example.onuldo_fe.ui.theme.Red2
import com.example.onuldo_fe.ui.theme.White

@Composable
fun ChallengeVerificationScreen(
    status: VerificationStatus
) {
    when (status) {
        VerificationStatus.REVIEWING -> {
            VerificationReviewingContent()
        }

        VerificationStatus.SUCCESS -> {
            VerificationSuccessContent()
        }

        VerificationStatus.FAILURE -> {
            VerificationFailureContent()
        }
        VerificationStatus.WAITING -> {
            VerificationWaitingContent()
        }
    }
}


@Composable
private fun VerificationReviewingContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 27.dp)
                    .height(48.dp)
            ) {
                OnulDoBackButton(
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "인증 결과",
                    color = BlackBrown,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            Image(
                painter = painterResource(
                    id = R.drawable.verification_reviewing_icon
                ),
                contentDescription = "인증 검토 아이콘",
                modifier = Modifier
                    .padding(top = 57.dp)
                    .size(120.dp)
            )

            Text(
                text = "AI가 인증 사진을 확인하고 있어요",
                color = BlackBrown,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "잠시만 기다려주세요! (예상 소요 시간 5초)",
                color = DarkBrown70,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(42.dp))

            // 추후 1,2,3,4,5,6 -> 체크 표시로 넘어가는 애니메이션 작성 (현재는 체크로 일괄 표시)
            VerificationStepCard(
                icon = R.drawable.verification_check_icon,
                title = "사진 메타데이터 검증",
            )

            VerificationStepCard(
                icon = R.drawable.verification_check_icon,
                title = "AI 이미지 전송",
            )

            VerificationStepCard(
                icon = R.drawable.verification_check_icon,
                title = "물체·활동 감지",
            )

            VerificationStepCard(
                icon = R.drawable.verification_check_icon,
                title = "신뢰도 필터링",
            )

            VerificationStepCard(
                icon = R.drawable.verification_check_icon,
                title = "챌린지 조건 분석",
            )

            VerificationStepCard(
                icon = R.drawable.verification_check_icon,
                title = "최종 결과 판정",
            )
        }
    }
}

@Composable
private fun VerificationSuccessContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 27.dp)
                    .height(48.dp)
            ) {
                OnulDoBackButton(
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "인증 결과",
                    color = BlackBrown,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            Image(
                painter = painterResource(
                    id = R.drawable.verification_success_icon
                ),
                contentDescription = "인증 성공 아이콘",
                modifier = Modifier
                    .padding(top = 243.dp)
                    .size(135.dp)
            )

            Text(
                text = "인증 성공!",
                color = BlackBrown,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "AI 검증을 모두 통과했어요",
                color = DarkBrown70,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 11.dp),
                textAlign = TextAlign.Center
            )
        }

        OnulDoButton(
            text = "확인",
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 42.dp)
        )
    }
}
@Composable
private fun VerificationFailureContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 27.dp)
                    .height(48.dp)
            ) {
                OnulDoBackButton(
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "인증 결과",
                    color = BlackBrown,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            Image(
                painter = painterResource(
                    id = R.drawable.verification_failure_icon
                ),
                contentDescription = "인증 실패 아이콘",
                modifier = Modifier
                    .padding(top = 57.dp)
                    .size(120.dp)
            )

            Text(
                text = "인증에 실패했어요",
                color = BlackBrown,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "AI 검증에서 미션 조건을 확인하지 못했어요",
                color = DarkBrown70,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 37.dp),
                shape = RoundedCornerShape(size = 14.dp),
                color = Red2
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "실패 사유",
                            color = Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(11.dp))

                    Text(
                        text = "사진에서 사람을 감지할 수 없어요",
                        color = BlackBrown,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                shape = RoundedCornerShape(size = 14.dp),
                color = Persimmon10,
                border = BorderStroke(
                        width = 1.dp,
                    color = Persimmon20
                )
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "남은 인증 시간",
                            color = Persimmon,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(9.dp))

                    //추후 남은시간 계산 로직 추가
                    Text(
                        text = "22분 남았어요. 재인증해보세요!",
                        color = BlackBrown,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 22.dp)
                    )
                }
            }
            Text(
                text = "AI 판정에 동의하지 않으시나요?",
                color = DarkBrown70,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 28.dp)
            )
            Text(
                text = "직접검토 요청하기",
                color = Persimmon,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(top = 7.dp)
                    .clickable{ }
            )
        }

        OnulDoButton(
            text = "다시 인증하기",
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 42.dp)
        )
    }
}

@Composable
private fun VerificationWaitingContent() {
    // 검토 대기 UI
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 27.dp)
                    .height(48.dp)
            ) {
                OnulDoBackButton(
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "인증 결과",
                    color = BlackBrown,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            Image(
                painter = painterResource(
                    id = R.drawable.verification_waiting_icon
                ),
                contentDescription = "인증 대기 아이콘",
                modifier = Modifier
                    .padding(top = 57.dp)
                    .size(120.dp)
            )

            Text(
                text = "인증 검토 중이에요",
                color = BlackBrown,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "AI가 판단하기 어려운 사진이라\n" +
                        "운영팀에서 직접 확인하고 있어요!",
                color = DarkBrown70,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 61.dp),
                shape = RoundedCornerShape(size = 14.dp),
                color = Red2
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "검토 안내",
                            color = BlackBrown,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(11.dp))

                    Text(
                        text = "검토는 최대 24시간 이내 완료됩니다",
                        color = DarkBrown,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )

                    Text(
                        text = "검토 중에도 챌린지는 계속 진행됩니다",
                        color = DarkBrown,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )

                    Text(
                        text = "결과는 알림으로 즉시 알려드려요",
                        color = DarkBrown,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )

                    Text(
                        text = "검토 통과 시 인증 완료 처리됩니다",
                        color = DarkBrown,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(size = 14.dp),
                color = White,
                border = BorderStroke(
                    width = 1.dp,
                    color = DarkBrown40
                )
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "제출시각",
                            color = DarkBrown,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        //추후 제출 시각 표현
                        Text(
                            text = "2026년 5월 20일 07:32",
                            color = BlackBrown,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }

        OnulDoButton(
            text = "확인",
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 42.dp)
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun VerificationReviewingContentPreview() {
    OnulDo_FETheme {
        ChallengeVerificationScreen(
            status = VerificationStatus.WAITING
        )
    }
}
