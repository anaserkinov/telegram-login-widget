# Telegram Login Widget

A Kotlin Multiplatform library that brings Telegram authentication to Android and iOS using Compose Multiplatform. It supports two official Telegram login methods — pick the one that fits your use case.

---

## Login Methods

| Method | Artifact |
|--------|----------|
| [Telegram Login / OpenID Connect](#telegram-login--openid-connect) | `me.anasmusa:telegram-login` |
| [Telegram Login Widget](#telegram-login-widget-legacy) | `me.anasmusa:telegram-login-widget` |

---

## Platforms

| Platform | Supported |
|----------|-----------|
| Android  | ✅        |
| iOS      | ✅        |

Looking for the iOS / SwiftUI version? Check out the [Swift package](https://github.com/anaserkinov/telegram-login-widget-swift).

---

## Telegram Login / OpenID Connect

The modern Telegram login flow ([Login via Telegram](https://core.telegram.org/bots/telegram-login)). It presents a Telegram-hosted dialog where the user confirms login with a single tap, then hands back an ID token and profile data to your app.

<p align="center">
  <img src="/assets/images/sample.gif" alt="Telegram Login demo" width="512"/>
</p>

### Installation

```kotlin
implementation("me.anasmusa:telegram-login:<version>")
```

### Setup

Follow Telegram's official guide to set up your bot and obtain a `client_id`: [Setting up a bot](https://core.telegram.org/bots/telegram-login#setting-up-a-bot).

### Usage

#### Basic Button

```kotlin
@Composable
fun LoginScreen(onResult: (TelegramLoginResult) -> Unit) {
    TelegramLoginButton(
        config = TelegramLoginConfig(
            clientId = 123456789L,
            redirectURI = "https://yourapp.com/callback",
        ),
        onResult = onResult,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Log in with Telegram")
    }
}
```

Tapping the button opens a `TelegramLoginDialog` with the Telegram OAuth WebView. Once the user authenticates, `onResult` is called with either `TelegramLoginResult.Success` or `TelegramLoginResult.Cancelled`.

---

#### Handling the Result

```kotlin
TelegramLoginButton(
    config = config,
    onResult = { result ->
        when (result) {
            is TelegramLoginResult.Success -> {
                val user = result.user
                println("Logged in as ${user.name} (id=${user.id})")
                // result.idToken  — raw JWT ID token for server-side validation
                // user.id, user.name, user.preferredUsername,
                // user.picture, user.phoneNumber, user.nonce,
                // user.iss, user.aud, user.iat, user.exp
            }
            is TelegramLoginResult.Cancelled -> {
                println("Login cancelled")
            }
        }
    },
    modifier = Modifier.fillMaxWidth(),
) {
    Text("Log in with Telegram")
}
```

---

You can also trigger the login flow from any custom UI element using `TelegramLoginDialog` directly:

```kotlin
var showDialog by remember { mutableStateOf(false) }

FilledIconButton(onClick = { showDialog = true }) {
    Icon(imageVector = TelegramDefaults.icon, contentDescription = null)
}

if (showDialog) {
    TelegramLoginDialog(
        config = config,
        onResult = { result ->
            showDialog = false
            onResult(result)
        },
    )
}
```

If you need to embed the OAuth WebView directly into your own layout without any button or dialog, use `TelegramLoginView`:

```kotlin
TelegramLoginView(
    config = TelegramLoginConfig(
        clientId = 123456789L,
        redirectURI = "https://yourapp.com/callback",
    ),
    modifier = Modifier.fillMaxSize(),
    onResult = onResult,
)
```

---

### API Reference

#### `TelegramLoginConfig`

```kotlin
data class TelegramLoginConfig(
    val clientId: Long,
    val redirectURI: String,
    val requestDirectMessages: Boolean = true, // request permission to send direct messages to the user (bot_access scope)
    val requestPhoneNumber: Boolean = false,   // request access to the user's phone number (phone scope)
    val nonce: String? = null,                 // optional nonce to protect against ID token replay attacks
    val languageCode: String? = null,
    val uiMode: UiMode = UiMode.Unspecified,   // Light, Dark, or system/default
)
```

#### `TelegramLoginResult`

```kotlin
sealed interface TelegramLoginResult {
    data class Success(
        val idToken: String,          // an OIDC JWT token containing user claims — validate on your server
        val user: TelegramUserData,   // Decoded id_token, containing the requested user info
    ) : TelegramLoginResult

    data object Cancelled : TelegramLoginResult
}

data class TelegramUserData(
    val id: Long,
    val name: String,
    val preferredUsername: String?,
    val picture: String?,
    val phoneNumber: String?,
    val nonce: String?,
    // JWT standard claims
    val iss: String,
    val aud: String,
    val iat: Long,
    val exp: Long,
)
```

#### `UiMode`

```kotlin
enum class UiMode { Light, Dark, Unspecified }
```

---

## Telegram Login Widget

The classic [Telegram Login Widget](https://core.telegram.org/widgets/login) flow. It provides ready-to-use, fully customizable login buttons backed by Telegram's official OAuth flow. Requires a bot and a registered website domain.

<img src="/assets/images/buttons_light.webp" alt="Buttons light" />

### Installation

```kotlin
implementation("me.anasmusa:telegram-login-widget:<version>")
```

### Setup

#### 1. Create a Telegram Bot

If you don't have a bot yet, create one via [@BotFather](https://t.me/BotFather) and note the **bot ID** and **bot username**.

To find your bot ID, open the following URL in any browser (replace `YOUR_BOT_TOKEN` with your actual token):

```
https://api.telegram.org/botYOUR_BOT_TOKEN/getMe
```

#### 2. Configure the Login Widget in BotFather

Send `/setdomain` to BotFather, select your bot, and enter the domain of the website you'll be authorizing against (e.g. `yourdomain.com`). This is required by Telegram's login widget.

---

### Usage

#### Basic Button

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

Tapping the button opens a `ModalBottomSheet` with Telegram's login WebView. Once the user authenticates, `onResult` is called with either `TelegramLoginResult.Success` or `TelegramLoginResult.Cancelled`.

---

#### Handling the Result

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

#### Outlined Button

```kotlin
TelegramLoginOutlinedButton(
    state = state,
    onResult = onResult,
    modifier = Modifier.fillMaxWidth(),
)
```

---

#### Customizing the Button

Both button composables are fully customizable — swap out any slot, change colors, replace the icon style, adjust avatar position, or compose entirely custom content.

<img src="/assets/images/buttons_dark.webp" alt="Buttons dark" />

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
            TelegramButtonText(
                state = it,
                modifier = Modifier.weight(weight = 1f, fill = false),
            )
            TelegramButtonUserPhotoBox(state = it)
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
        websiteUrl = "https://yourdomain.com",
    ),
    modifier = Modifier.fillMaxSize(),
    onResult = onResult,
)
```

---

#### Logout

```kotlin
// If you have a state object
state.logout()

// If you don't have a state object
TelegramLoginManager.logout()
```

This clears all Telegram cookies and resets the button to its pre-login appearance.

---

### API Reference

#### `rememberTelegramLoginState`

```kotlin
fun rememberTelegramLoginState(
    botId: Long,
    botUsername: String,
    websiteUrl: String,
    requestAccess: Boolean = true,   // request permission to send messages
    languageCode: String = "en",
): TelegramLoginState
```

#### `TelegramLoginState`

| Member | Type | Description |
|--------|------|-------------|
| `config` | `TelegramLoginConfig` | The configuration used to initialize the widget |
| `isLoading` | `Boolean` | `true` while button content or user photo is being fetched |
| `buttonContent` | `ButtonContent` | Current text, first name, and avatar painter |
| `reload()` | `fun` | Re-fetches button state (call after a login result) |
| `logout()` | `fun` | Clears session and resets button |

#### `TelegramLoginResult`

```kotlin
sealed interface TelegramLoginResult {
    data class Success(
        val id: Long,
        val firstName: String,
        val lastName: String?,
        val username: String?,
        val photoUrl: String?,
        val authDate: Long,
        val hash: String,         // verify on your server
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
| iOS targets | `iosArm64`, `iosSimulatorArm64`, `iosX64` |

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