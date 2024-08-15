import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.MiuixNavigationBar
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.MiuixTopAppBar
import top.yukonga.miuix.kmp.NavigationItem
import top.yukonga.miuix.kmp.basic.MiuixHorizontalPager
import top.yukonga.miuix.kmp.basic.MiuixScaffold
import top.yukonga.miuix.kmp.basic.MiuixSurface
import top.yukonga.miuix.kmp.rememberMiuixTopAppBarState
import top.yukonga.miuix.kmp.utils.MiuixDialogUtil.Companion.MiuixDialogHost

@OptIn(FlowPreview::class)
@Composable
fun UITest() {
    val topAppBarScrollBehavior0 = MiuixScrollBehavior(rememberMiuixTopAppBarState())
    val topAppBarScrollBehavior1 = MiuixScrollBehavior(rememberMiuixTopAppBarState())
    val topAppBarScrollBehavior2 = MiuixScrollBehavior(rememberMiuixTopAppBarState())
    val topAppBarScrollBehavior3 = MiuixScrollBehavior(rememberMiuixTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(
        topAppBarScrollBehavior0, topAppBarScrollBehavior1, topAppBarScrollBehavior2, topAppBarScrollBehavior3
    )

    val pagerState = rememberPagerState(pageCount = { 4 })
    var targetPage by remember { mutableStateOf(pagerState.currentPage) }
    val coroutineScope = rememberCoroutineScope()

    val currentScrollBehavior = when (pagerState.currentPage) {
        0 -> topAppBarScrollBehaviorList[0]
        1 -> topAppBarScrollBehaviorList[1]
        2 -> topAppBarScrollBehaviorList[2]
        3 -> topAppBarScrollBehaviorList[3]
        else -> throw IllegalStateException("Unsupported page")
    }

    val items = listOf(
        NavigationItem("Main", Icons.Default.Phone),
        NavigationItem("Second", Icons.Default.Email),
        NavigationItem("Third", Icons.Default.Place),
        NavigationItem("Fourth", Icons.Default.Settings)
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.debounce(150).collectLatest {
            targetPage = pagerState.currentPage
        }
    }

    MiuixSurface {
        MiuixScaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                MiuixTopAppBar(
                    color = Color.Transparent,
                    title = "Miuix",
                    scrollBehavior = currentScrollBehavior
                )
            },
            bottomBar = {
                MiuixNavigationBar(
                    color = Color.Transparent,
                    items = items,
                    selected = targetPage,
                    onClick = { index ->
                        targetPage = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            },
            dialogHost = { MiuixDialogHost() }
        ) { padding ->
            AppHorizontalPager(
                pagerState = pagerState,
                topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
                padding = padding
            )
        }
    }
}

@Composable
fun AppHorizontalPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    topAppBarScrollBehaviorList: List<MiuixScrollBehavior>,
    padding: PaddingValues,
) {
    MiuixHorizontalPager(
        modifier = modifier,
        pagerState = pagerState,
        pageContent = { page ->
            when (page) {
                0 -> MainPage(topAppBarScrollBehaviorList[0], padding)
                1 -> SecondPage(topAppBarScrollBehaviorList[1], padding)
                2 -> ThirdPage(topAppBarScrollBehaviorList[2], padding)
                else -> FourthPage(topAppBarScrollBehaviorList[3], padding)
            }
        }
    )
}