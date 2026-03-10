# Telegram Login Widget

A Kotlin Multiplatform library that brings [Telegram Login Widget](https://core.telegram.org/widgets/login) to Android and iOS using Compose Multiplatform. It provides ready-to-use, fully customizable login buttons backed by Telegram's official OAuth flow.

<img src="/assets/images/buttons_light.webp"  alt="Buttons"/>

## Platforms

| Platform | Supported |
|----------|-----------|
| Android  | ✅        |
| iOS      | ✅        |

Looking for the iOS / SwiftUI version? Check out the [Swift package](https://github.com/anaserkinov/telegram-login-widget-swift).

---

## Installation

Add the dependency to your project:

```kotlin
implementation("me.anasmusa:telegram-login-widget:<version>")
```

---

## Setup

### 1. Create a Telegram Bot

If you don't have a bot yet, create one via [@BotFather](https://t.me/BotFather) and note the **bot ID** and **bot username**.

To find your bot ID, open the following URL in any browser (replace `YOUR_BOT_TOKEN` with your actual token):

```
https://api.telegram.org/botYOUR_BOT_TOKEN/getMe
```

### 2. Configure the Login Widget in BotFather

Send `/setdomain` to BotFather, select your bot, and enter the domain of the website you'll be authorizing against (e.g. `yourdomain.com`). This is required by Telegram's login widget.

---

## Usage

### Basic Button

```kotlin
@Composable
fun LoginScreen(onResult: (TelegramLoginResult) -> Unit) {
    val state = rememberTelegramLoginState(
        botId = 123456789L,
        botUsername = "your_bot",
        websiteUrl = "https://yourdomain.com",
    )

    TelegramLoginButton(
        state = state,
        onResult = onResult,
        modifier = Modifier.fillMaxWidth(),
    )
}
```

Tapping the button opens a `ModalBottomSheet` with Telegram's login WebView. Once the user authenticates, `onResult` is called with either a `TelegramLoginResult.Success` or `TelegramLoginResult.Cancelled`.

---

### Handling the Result

```kotlin
TelegramLoginButton(
    state = state,
    onResult = { result ->
        when (result) {
            is TelegramLoginResult.Success -> {
                println("Logged in as ${result.firstName} (id=${result.id})")
                // result.id, result.firstName, result.lastName,
                // result.username, result.photoUrl, result.authDate, result.hash
            }
            is TelegramLoginResult.Cancelled -> {
                println("Login cancelled")
            }
        }
    },
)
```

---

### Outlined Button

```kotlin
TelegramLoginOutlinedButton(
    state = state,
    onResult = onResult,
    modifier = Modifier.fillMaxWidth(),
)
```

---

### Customizing Button

Both button composables are fully customizable — you can swap out any part of the button's appearance, change colors, replace the icon style, adjust the avatar position, or compose entirely custom content.

<img src="/assets/images/buttons_dark.webp"  alt="Buttons"/>

```kotlin
// Light-themed button with Telegram-colored icon
TelegramLoginButton(
    state = state,
    onResult = onResult,
    modifier = Modifier.fillMaxWidth(),
    left = {
        TelegramButtonIcon(tint = TelegramDefaults.primaryColor)
    },
    colors = TelegramDefaults.buttonColors(
        containerColor = Color.White,
        contentColor = Color.Black,
    ),
)
```

```kotlin
// Centered text with the user avatar placed next to the label
TelegramLoginButton(
    state = state,
    onResult = onResult,
    modifier = Modifier.fillMaxWidth(),
    center = {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TelegramButtonText(state = it)
            TelegramButtonUserPhotoBox(state = it, modifier = Modifier.padding(start = 8.dp))
        }
    },
    right = null,
)
```

You can also trigger the login flow from any custom UI element using `TelegramLoginBottomSheet` directly, without a button:

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

FilledIconButton(onClick = { showBottomSheet = true }) {
    Icon(imageVector = TelegramDefaults.icon, contentDescription = null)
}

if (showBottomSheet) {
    TelegramLoginBottomSheet(
        config = state.config,
        onResult = { result ->
            showBottomSheet = false
            state.reload()
            onResult(result)
        },
    )
}
```

If you need to embed the Telegram OAuth WebView directly into your own layout without any button or bottom sheet, use `TelegramLoginView`:

```kotlin
TelegramLoginView(
    config = TelegramLoginConfig(
        botId = 123456789,
        botUsername = "your_bot",
        websiteUrl = "https://yourdomain.com"
    ),
    modifier = Modifier.fillMaxSize(),
    onResult = onResult,
)
```

---

### Logout

```kotlin
// If you have a state object
state.logout()

// If you don't have a state object
TelegramLoginManager.logout()
```

This clears all Telegram cookies and resets the button to its pre-login appearance.

---

## API Reference

### `rememberTelegramLoginState`

```kotlin
fun rememberTelegramLoginState(
    botId: Long,
    botUsername: String,
    websiteUrl: String,
    requestAccess: Boolean = true,   // request permission to send messages
    languageCode: String = "en",
): TelegramLoginState
```

### `TelegramLoginState`

| Member | Type | Description |
|--------|------|-------------|
| `config` | `TelegramLoginConfig` | The configuration used to initialize the widget |
| `isLoading` | `Boolean` | `true` while button content or user photo is being fetched |
| `buttonContent` | `ButtonContent` | Current text (may be empty before first successful load), first name, and avatar painter |
| `reload()` | `fun` | Re-fetches button state (call after a login result) |
| `logout()` | `fun` | Clears session and resets button |

### `TelegramLoginResult`

```kotlin
sealed interface TelegramLoginResult {
    data class Success(
        val id: Long,
        val firstName: String,
        val lastName: String?,
        val username: String?,
        val photoUrl: String?,
        val authDate: Long,
        val hash: String,
    ) : TelegramLoginResult

    data object Cancelled : TelegramLoginResult
}
```

---

## Samples

The repository includes two sample apps:

- **`samples/compose-multiplatform`** — a shared Compose Multiplatform app running on Android and iOS
- **`samples/android-native`** — a native Android app using the library directly

---

## Requirements

| Tool | Minimum Version |
|------|----------------|
| Android `minSdk` | 24 |
| Android `compileSdk` | 36 |
| Kotlin | 2.3 |
| Compose Multiplatform | 1.10 |
| iOS targets | `iosArm64`, `iosSimulatorArm64` |

---

## License

```
MIT License

Copyright (c) 2026 Anas (anaserkinjonov@gmail.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
