package top.yukonga.miuix.uitest

import App
import MainPage
import SecondPage
import ThirdPage
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import ui.AppTheme

@Composable
@Preview(device = "spec:width=1080px,height=2340px,dpi=480")
fun UITestPreview() {
    AppTheme {
        Scaffold {
            App()
        }
    }
}

@Composable
@Preview
fun MainPagePreview() {
    AppTheme {
        Scaffold {
            MainPage(MiuixScrollBehavior(rememberTopAppBarState()), PaddingValues())
        }
    }
}

@Composable
@Preview
fun SecondPagePreview() {
    AppTheme {
        Scaffold {
            SecondPage(MiuixScrollBehavior(rememberTopAppBarState()), PaddingValues())
        }
    }
}

@Composable
@Preview
fun ThirdPagePreview() {
    AppTheme {
        Scaffold {
            ThirdPage(
                MiuixScrollBehavior(rememberTopAppBarState()),
                PaddingValues(),
                false,
                {},
                false,
                {},
                false,
                {},
                false,
                {},
                false,
                {},
                remember { mutableIntStateOf(0) }
            )
        }
    }
}