# Orbit Dash — Figma AI Design Brief

---

## 1. Art Style & Color Palette

**Art Style:** Flat geometric design with subtle depth created through layered shapes and minimal drop shadows. Clean, modern aesthetic with a sci-fi theme emphasizing orbital mechanics and cosmic environments. No gradients on UI elements themselves; reserved for backgrounds and accent effects. All typography is sans-serif, prioritizing clarity and readability at small mobile sizes.

**Primary Color Palette:**
- Deep Space Navy: `#0A1428` (primary background, dark UI surfaces)
- Vibrant Cyan: `#00D4FF` (primary interactive elements, ring highlights, ship accent)
- Neon Orange: `#FF6B35` (secondary accent, asteroids, warning states)
- Soft White: `#F5F5F5` (primary text, UI contrast)

**Accent Colors:**
- Electric Purple: `#B13AFF` (tertiary interactive states, star collectibles, unlock highlights)
- Warm Gold: `#FFD700` (score values, premium currency indication, victory states)

**Font Mood & Weight:** Modern, futuristic sans-serif (equivalent to Inter, Roboto, or Sofia Pro). Headlines use Bold (700) weight for authority; body text uses Regular (400) for legibility; buttons use Medium (600) for emphasis. All text maintains minimum 12px size on mobile with 1.2 line-height for comfortable reading in arcade-paced contexts.

---

## 2. App Icon — icon_512.png (512×512px)

**Background:** Radial gradient from Deep Space Navy (`#0A1428`) at edges to a lighter blue-black (`#1a2a4a`) at center, suggesting the vacuum of space with depth.

**Central Symbol:** A stylized planet (perfect circle) in the center, rendered in gradient from Vibrant Cyan (`#00D4FF`) to Electric Purple (`#B13AFF`), with subtle horizontal ring lines at 40% and 70% radius to indicate orbital rings. A small spaceship silhouette (angular, triangular profile) positioned on the outermost ring, angled 45° as if in motion. The three concentric rings are outlined in Vibrant Cyan with decreasing opacity moving outward.

**Glow & Shadow Effects:** Subtle inner shadow on the planet (2–3px, dark purple at 30% opacity) to create curvature. A soft outer glow around the planet edge (4px blur, Vibrant Cyan at 20% opacity) suggesting energy. The ship has a small trailing motion effect: 2–3 small circles in graduated Cyan-to-transparent, trailing behind the ship's trajectory.

**Overall Mood:** Dynamic, futuristic, immediately communicates "orbital mechanics" and arcade energy. Professional but playful, inviting repeated interaction.

---

## 3. Backgrounds (480×854 portrait)

**Background List (derived from game description):**
1. Main Menu / Title Screen: `backgrounds/bg_main.png`
2. Default Planet World (starter): `backgrounds/bg_planet_default.png`
3. Lava Planet World (first unlock): `backgrounds/bg_planet_lava.png`
4. Ice Planet World (second unlock): `backgrounds/bg_planet_ice.png`
5. Generic UI Modal Backdrop: `backgrounds/bg_modal_dark.png`

---

### backgrounds/bg_main.png (480×854)
A rich, starfield backdrop featuring a gradient from Deep Space Navy (`#0A1428`) at the top to a slightly lighter navy (`#0f1f3c`) at the bottom, suggesting atmospheric depth. Scattered white stars of varying sizes (1–4px) distributed across the entire canvas in a pseudo-random pattern, denser toward top edges. A faint nebula accent (Vibrant Cyan at 8% opacity) in the lower-right corner, blurred and non-intrusive. The central area remains clean to allow prominent title and menu buttons. No visible horizon line or planet—emphasis is on the vastness of space.

### backgrounds/bg_planet_default.png (480×854)
Centered, large cyan-and-purple gradient planet occupying roughly 300×300px in the middle-upper area, with subtle cloud-like texture overlay using semi-transparent white. Three concentric orbital rings rendered as thin cyan lines (`#00D4FF`, 2px stroke) at radii 120px, 160px, and 200px, rotating imperceptibly to suggest motion. Background gradient beneath the planet shifts from Deep Space Navy (`#0A1428`) at top to dark blue-black (`#1a2a4a`) at bottom. Scattered asteroid shapes (small dark grey polygons with slight glow) hinting at the gameplay danger. Overall mood: inviting, balanced, canonical to the arcade game's core visual identity.

### backgrounds/bg_planet_lava.png (480×854)
A large planet dominates the center-upper area, rendered in hot-color gradients: fiery orange (`#FF6B35`) to deep crimson red (`#8B1A1A`), with cracked surface texture suggesting volcanic activity. Rings are rendered in orange-tinted cyan (`#FF8C42`), maintaining the lava world aesthetic. Background gradient transitions from dark red-brown (`#2a0f0f`) at top to charred black (`#0a0a0a`) at bottom. Floating magma particles (small orange circles, semi-transparent) drift slowly across the canvas. Small volcanic asteroid shapes cluster near the planet surface. Mood: intense, dangerous, high-energy—signals a more challenging difficulty tier.

### backgrounds/bg_planet_ice.png (480×854)
A frosted, icy planet centered in the upper-middle area, rendered in cool palette: pale cyan (`#B0E0E6`) to silver-white (`#E8F4F8`), with crystalline texture overlay suggesting frozen surface. Rings rendered in bright cyan-white (`#E0FFFF`) with subtle iridescent shimmer (use layered semi-transparent strokes). Background gradient flows from deep indigo (`#191970`) at top to cool blue-black (`#0f1f4a`) at bottom. Floating ice shards (thin white lines at various angles) gently drift across the scene. Small icy asteroid shapes (angular, translucent white polygons) hint at hazards. Mood: calm, pristine, elegant—signals a slower, more contemplative gameplay variation while maintaining arcade energy.

### backgrounds/bg_modal_dark.png (480×854)
Solid Deep Space Navy (`#0A1428`) with a very subtle radial noise texture (1–2% opacity) to prevent flatness. Used as a semi-transparent overlay behind modals (shop, leaderboard, settings). No stars or decorative elements—serves as a clean contrast layer. Optional very subtle vignette effect (darker edges) to focus attention on modal content.

---

## 4. UI Screens (480×854 portrait)

### MainMenuScreen
Uses `backgrounds/bg_main.png`. Centered at top (y: ~80px) is the title "ORBIT DASH" in Bold Roboto or Sofia Pro, 56px, Soft White (`#F5F5F5`), all caps, with a subtle Vibrant Cyan (`#00D4FF`) text shadow (2px offset down-right, 2px blur) to suggest a glowing holographic effect. Below the title (y: ~160px) is a call-to-action subtitle "TAP TO ORBIT" in Regular weight, 18px, Electric Purple (`#B13AFF`), all caps.

Primary action button "PLAY" positioned center (y: ~320px), 100px wide, 48px tall, background Vibrant Cyan (`#00D4FF`), text Deep Space Navy (`#0A1428`), Medium weight, 20px. Secondary buttons arranged vertically below PLAY (y: ~400–600px, spaced 60px apart): "SHOP" (text Electric Purple, no background, underline Warm Gold), "WORLDS" (text Electric Purple), "LEADERBOARD" (text Electric Purple), "HOW TO PLAY" (text Electric Purple). Bottom-right corner (y: ~780px) houses a small Settings gear icon (18×18px, Soft White) as a touch target.

Score badge positioned top-right (y: ~20px), displaying "BEST: [score]" in Regular 14px Soft White. Current star balance ("⭐ [count]") displayed top-center, 14px Regular, Warm Gold.

---

### GameScreen — Default Planet Variant: game_default.png
Uses `backgrounds/bg_planet_default.png`. Core gameplay elements center-stage: large planet (300px diameter) positioned at canvas center (240, 320), three cyan orbital rings concentric around it (120px, 160px, 200px radius). The spaceship appears as a small angular silhouette (~12px wide) positioned on one of the three rings, rotating smoothly with the planet's rotation.

Asteroids render as irregular dark-grey polygons (~18–24px) appearing on rings before collision; stars render as bright Electric Purple (`#B13AFF`) 5-pointed stars (~16px) flickering on rings to indicate collectibles. HUD display top-left: "SCORE: [value]" in Regular 16px Soft White. Top-right: star balance "⭐ [count]" in Warm Gold 14px. Small pause button (circular, 40×40px) positioned top-right corner, icon Soft White on transparent or subtle cyan background.

Overall mood: focused, arcade-immediate, clear visual hierarchy favoring the central orbital mechanic.

---

### GameScreen — Lava Planet Variant: game_lava.png
Uses `backgrounds/bg_planet_lava.png`. Identical layout and HUD positioning as game_default.png, but planet rendered in hot-color gradients (orange to crimson), rings in warm orange-cyan (`#FF8C42`). Asteroids appear slightly more aggressive (more frequent, slightly larger). Star collectibles render in warm gold (`#FFD700`) rather than purple to harmonize with lava theme. Drifting magma particles in background reinforce danger and intensity. HUD text remains Soft White for clarity.

---

### GameScreen — Ice Planet Variant: game_ice.png
Uses `backgrounds/bg_planet_ice.png`. Identical layout as game_default.png, but planet rendered in cool frosted palette (pale cyan to silver). Rings glow in bright cyan-white (`#E0FFFF`). Asteroids appear as translucent icy polygons. Star collectibles render in pale cyan (`#B0E0E6`) to match the frozen aesthetic. Floating ice shards animate across the background subtly. HUD remains unchanged (Soft White text for maximum contrast against cool backgrounds).

---

### GameOverScreen
Uses `backgrounds/bg_modal_dark.png` with semi-transparent overlay (60% opacity) over the last active GameScreen variant. Centered modal card (360px wide, 420px tall, background Deep Space Navy `#0A1428`, 12px rounded corners, 2px Vibrant Cyan border).

Modal title: "GAME OVER" in Bold 40px, Neon Orange (`#FF6B35`), centered (y: ~30px relative to modal). Large score display below (y: ~100px): "FINAL SCORE" in Regular 14px Soft White, then "[score]" in Bold 48px Warm Gold (`#FFD700`). Comparison line (y: ~180px): "BEST SCORE: [best]" in Regular 12px Soft White if current < best, or "NEW BEST! 🎉" in Electric Purple if current >= best.

Two action buttons at bottom of modal (y: ~340px): Left button "RESTART" (60px wide, 40px tall, background Vibrant Cyan, text Deep Space Navy), right button "MENU" (60px wide, 40px tall, background Electric Purple, text Soft White), spaced 20px apart, centered horizontally within modal.

Small star reward display (y: ~280px, center): "EARNED: +[stars] ⭐" in Regular 16px Warm Gold (reward scales with final score).

---

### ShopScreen
Uses `backgrounds/bg_modal_dark.png` as full-screen backdrop. Top header bar (480×70px) with title "SHIP SKINS" in Bold 32px Soft White, left-aligned (x: ~20px). Top-right corner displays star balance "⭐ [count]" in Warm Gold 14px within a small pill-shaped container (60×28px, background Deep Space Navy, border Warm Gold).

Main content area displays a 2×3 grid of ship skin cards (starting y: ~100px). Each card is 140×180px, background slightly lighter navy (`#0f1f3c`), border 2px Vibrant Cyan if unlocked / Neon Orange if locked. Card shows: centered ship silhouette preview (60×60px), card name below (Regular 12px Soft White), cost or "EQUIPPED" label below name (Regular 11px, cost in Warm Gold if purchasable, "EQUIPPED" in Electric Purple if active). Tap card to open a purchase confirmation modal or equip the skin.

Bottom navigation: "Back" button (left-aligned, y: ~780px, text Electric Purple, underlined Warm Gold).

---

### LeaderboardScreen
Uses `backgrounds/bg_modal_dark.png` as full-screen backdrop. Top header: "LEADERBOARD" in Bold 32px Soft White, left-aligned (x: ~20px, y: ~20px). Subtitle "TOP 10 ALL TIME" in Regular 12px Electric Purple (x: ~20px, y: ~60px).

Main list area (y: ~90px–y: ~750px) displays up to 10 rows. Each row is 440px wide (left-aligned x: ~20px), 50px tall, background alternating Deep Space Navy / slightly lighter navy for contrast. Row layout (left to right): rank badge (e.g., "1.", 20px right-aligned within 40px column, Warm Gold Bold), player name (Regular 14px Soft White, 200px column), score value (Bold 16px Vibrant Cyan, 120px right-aligned column), date (Regular 11px Electric Purple, 80px right-aligned column). Subtle 1px bottom border between rows in Vibrant Cyan at 20% opacity.

Bottom navigation: "Back" button (left-aligned, y: ~780px, text Electric Purple, underlined Warm Gold).

---

### WorldsScreen
Uses `backgrounds/bg_main.png`. Top header: "WORLDS" in Bold 32px Soft White, centered (y: ~20px). Subtitle: "UNLOCK NEW PLANETS" in Regular 12px Electric Purple, centered (y: ~60px).

Three world cards displayed vertically (y: ~110, ~280, ~450px, spaced 170px apart). Each card is 320px wide, 140px tall, centered horizontally, background Deep Space Navy (`#0A1428`), rounded corners 8px, border 2px Vibrant Cyan (unlocked) or Neon Orange (locked). Card layout: left side shows 80×80px preview thumbnail of planet (small circular render of Default/Lava/Ice planet), right side displays world name ("DEFAULT PLANET", "LAVA WORLD", "ICE WORLD") in Bold 18px Soft White (top), unlock requirement text below in Regular 12px Electric Purple (e.g., "UNLOCK AT 500 SCORE"), small lock icon (16×16px, Neon Orange) if locked, or "UNLOCKED ✓" in Vibrant Cyan if unlocked.

Bottom navigation: "Back" button (left-aligned, y: ~780px, text Electric Purple, underlined Warm Gold).

---

### SettingsScreen
Uses `backgrounds/bg_modal_dark.png` as full-screen backdrop. Top header: "SETTINGS" in Bold 32px Soft White, centered (y: ~30px).

Settings options displayed as toggle rows (starting y: ~120px, spaced 70px apart). Each row is 320px wide, centered horizontally, layout: left-aligned label text (Regular 14px Soft White, 200px column), right-aligned toggle switch (60×32px, background grey if off / Vibrant Cyan if on, circle indicator white). Four toggle rows:
1. "SOUND" (y: ~120px)
2. "MUSIC" (y: ~190px)
3. "VIBRATION" (y: ~260px)
4. "DIFFICULTY" (y: ~330px, uses dropdown: "EASY", "NORMAL", "HARD" in Regular 12px)

Credits section at bottom (y: ~600px): "MADE WITH ❤️" in Regular 12px Electric Purple, centered, followed by "v1.0.0" in Regular 10px Soft White (50% opacity).

Bottom navigation: "Back" button (left-aligned, y: ~780px, text Electric Purple, underlined Warm Gold).

---

### HowToPlayScreen
Uses `backgrounds/bg_main.png`. Top header: "HOW TO PLAY" in Bold 32px Soft White, centered (y: ~20px).

Content displayed as scrollable vertical stack (y: ~80–700px). Three main instruction sections, each 380px wide, centered horizontally, spacing 60px between sections:

**Section 1: "TAP TO JUMP"** (y: ~80px)
- Icon: simple ship on three concentric rings (80×80px, rendered in Vibrant Cyan)
- Text below (Regular 13px Soft White, center-aligned): "Tap the screen to jump your ship between orbital rings. Tap again to return."

**Section 2: "DODGE ASTEROIDS"** (y: ~220px)
- Icon: asteroid shape colliding with ring (80×80px, Neon Orange)
- Text: "Asteroids appear randomly on rings. Jump to another ring to avoid collision. Hit one = Game Over."

**Section 3: "COLLECT STARS"** (y: ~360px)
- Icon: bright 5-pointed star (80×80px, Electric Purple)
- Text: "Stars appear briefly on rings. Collect them to earn currency for the Ship Shop. More stars = more customization!"

**Section 4: "COMPLETE LAPS"** (y: ~500px)
- Icon: circular progress arc with checkmark (80×80px, Warm Gold)
- Text: "Your score increases with each full lap around the planet without crashing. Try to beat your best score and unlock new worlds!"

Bottom navigation: "Back" button (left-aligned, y: ~780px, text Electric Purple, underlined Warm Gold).

---

### SkinPreviewScreen
Uses `backgrounds/bg_planet_default.png` (regardless of active world). Full-screen display of the currently equipped ship skin. Large preview rendering occupies center area (y: ~120–500px), showing the ship model from a 3/4 isometric angle at ~200×200px scale. Ship rendered in full color with highlights and shadows to showcase cosmetic details (colors, decals, accessories).

Top-left corner (y: ~20px): skin name in Bold 24px Soft White. Top-right corner: "⭐ [cost]" in Warm Gold 12px (price if not yet owned, or omitted if owned).

Bottom banner (y: ~720–850px): two action buttons: left "BACK" (text Electric Purple, underlined), right "EQUIP" (background Vibrant Cyan, text Deep Space Navy) if not currently equipped, or "EQUIPPED ✓" (text Electric Purple) if active.

Optional: Small carousel dots at bottom-center (y: ~780px) if multiple skins, allowing swipe navigation left/right.

---

## 5. Export Checklist

- icon_512.png (512×512)
- backgrounds/bg_main.png (480×854)
- backgrounds/bg_planet_default.png (480×854)
- backgrounds/bg_planet_lava.png (480×854)
- backgrounds/bg_planet_ice.png (480×854)
- backgrounds/bg_modal_dark.png (480×854)
- ui/main_menu_screen.png (480×854)
- ui/game_default.png (480×854)
- ui/game_lava.png (480×854)
- ui/game_ice.png (480×854)
- ui/game_over_screen.png (480×854)
- ui/shop_screen.png (480×854)
- ui/leaderboard_screen.png (480×854)
- ui/worlds_screen.png (480×854)
- ui/settings_screen.png (480×854)
- ui/how_to_play_screen.png (480×854)
- ui/skin_preview_screen.png (480×854)

---

**Total files: 17**  
**All filenames are final and match Figma AI expectations.**
