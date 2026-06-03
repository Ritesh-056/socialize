# Socialize

Native Android app for connecting people around shared hobbies. Users sign in with Firebase Auth, pick interest categories, and chat in hobby group rooms or one-to-one private chats.

## Tech stack

| Layer | Choice |
|--------|--------|
| Language | Java |
| UI | AndroidX, Material, ViewPager + BottomNavigation |
| Backend | Firebase Auth, Cloud Firestore, Firebase Storage |
| Local cache | [Hawk](https://github.com/orhanobut/hawk) (current user profile) |
| Images | Picasso, Matisse (picker), Glide (Matisse engine) |

**Requirements:** Android Studio (Arctic Fox or newer recommended), JDK 8+, `minSdk` 22, `compileSdk` 31.

## Project structure

```
app/src/main/java/np/com/socialize/
├── ChatActivity.java          # Group + private message screen
├── ChatFragment.java          # Chat tab (Find Friends / Current Chat)
├── CurrentChatFragment.java   # List of existing private chats
├── FindFriendFragment.java    # User list → start/open private chat
├── PrivateChat.java           # Private chat room model
├── SocializeDashboardActivity.java
└── category/
    ├── ChatAdapter.java       # Message list UI
    ├── ChatMessage.java
    ├── CurrentChatAdapter.java
    └── UserDataViewModel.java # Firestore users + private chats
```

## Setup

1. **Clone** the repository.
2. **Firebase**
   - Create a project in the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with package name `np.com.socialize`.
   - Download `google-services.json` and place it in `app/` (replace the sample if present).
   - Enable **Authentication** (Phone, Email, Google, Facebook as used in the app).
   - Create **Firestore** and **Storage** in production or test mode, then tighten rules before release.
3. **Facebook login** (optional): update `facebook_app_id` and `fb_login_protocol_scheme` in `app/src/main/res/values/strings.xml` to match your Facebook app.
4. Open the project in Android Studio and run **Sync Project with Gradle Files**, then **Run** on a device or emulator.

### Firestore layout (chat-related)

| Collection | Document | Subcollection / fields |
|------------|----------|-------------------------|
| `categories` | `{serverId}` | `messages` — group hobby chat (`message`, `messageUser`, `profile`, `messageTime`, `senderId`, optional `imageMessage`) |
| `privateChat` | auto id | `members` (array of user ids), `sender`, `receiver`, `lastMessage`, `accepted`, … |
| `privateChat/{id}` | — | `messages` — same shape as category messages |
| `users` | `{uid}` | Profile fields (`name`, `profile_photo`, …) |

**Indexes:** Queries use `orderBy("messageTime")` on message subcollections and `whereArrayContains("members", uid)` on `privateChat`. Create composite indexes when the Firebase console prompts after the first run.

### Storage

Profile and chat images are uploaded under `uploads/{uuid}` in Firebase Storage.

## Chat flows

1. **Hobby group chat** — Dashboard → hobby tile → `ChatActivity` with `type=categories` (default).
2. **Private chat**
   - **Find Friends** tab → tap message icon → opens existing room or creates one, then opens `ChatActivity` with `type=privateChat`.
   - **Current Chat** tab → tap a row → opens the same activity with the stored `private_id`.

Messages are loaded in real time via Firestore snapshot listeners. Text and image messages are supported (images via Matisse + Storage upload).

## Permissions

- `INTERNET` — API calls  
- `READ_EXTERNAL_STORAGE` / `WRITE_EXTERNAL_STORAGE` — image picker (legacy storage; consider scoped storage updates for newer SDK targets)

## Known limitations

- Message **edit/delete** UI is partially wired (tap message fills the input) but not fully persisted.
- Private chats are created with `accepted=false`; accept/decline flow is not implemented.
- `targetSdk` / storage permissions may need updates for Android 13+.
- Some hobby/category assets (e.g. category images) are expected from Firestore, not all bundled under `res/drawable`.

## Build from command line

```bash
chmod +x gradlew
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

## License

No license file is included in this repository; add one if you plan to distribute the app.
