    package com.example.onuldo_fe.ui.screen.record.component
    
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.Surface
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import com.example.onuldo_fe.ui.screen.record.RecordTab
    import com.example.onuldo_fe.ui.theme.OnulDo_FETheme
    import com.example.onuldo_fe.ui.theme.Persimmon10
    
    @Composable
    fun RecordTabRow(
        selectedTab: RecordTab,
        onTabSelected:(RecordTab) -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(4.dp),
            color = Persimmon10
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    RecordTabItem(
                        title = "진행중",
                        selected = selectedTab == RecordTab.PROGRESS,
                        onClick = { onTabSelected(RecordTab.PROGRESS) }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    RecordTabItem(
                        title = "완료",
                        selected = selectedTab == RecordTab.COMPLETE,
                        onClick = { onTabSelected(RecordTab.COMPLETE) }
                    )
                }
            }
        }
    }

        @Preview(name = "진행중", showBackground = true)
        @Composable
        fun RecordTabRowProgressPreview() {
            OnulDo_FETheme {
                RecordTabRow(
                    selectedTab = RecordTab.PROGRESS,
                    onTabSelected = {}
                )
            }
        }

        @Preview(name = "완료", showBackground = true)
        @Composable
        fun RecordTabRowCompletePreview() {
            OnulDo_FETheme {
                RecordTabRow(
                    selectedTab = RecordTab.COMPLETE,
                    onTabSelected = {}
                )
            }
        }

