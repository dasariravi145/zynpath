# Zynpath Target Audience & Families Policy Decision Worksheet

**Status:** Authoritative  
**Domain:** Target Audience Evaluation & Google Play Families Policy Determination  

---

## 1. Decision Worksheet: Feature vs. Audience Assessment

| Application Feature | Nature of Feature | Suitability for Children (<13) | Implications Under Google Play Families Policy | Recommendation |
|---|---|---|---|---|
| **Core Puzzle Gameplay** | Number path connection, mathematical logic, spatial reasoning, zero violence. | **High** (Beneficial cognitive puzzle) | Compliant with child safety guidelines. | Universal appeal. |
| **Social Multiplayer (Quick Duel)** | Real-time 1v1 matchmaking with randomly matched online players. | **Moderate / Low** | Requires strict moderation. While preset-only communication prevents inappropriate text, random matchmaking requires parental consent under certain jurisdictions. | Recommend 13+. |
| **Communication Mechanism** | Strictly preset positive emojis and canned phrases (`"Good match!"`, `"Well played!"`). Zero free-text chat. | **High** (Safe by design) | Highly safe, but preset communication in random multiplayer still requires disclosure. | Compliant with 13+. |
| **Monetization (Subscriptions)** | Google Play Billing recurring subscriptions (monthly/6-months). | **Low** | Requires parental authorization; accidental in-app purchases by minors trigger strict refund and dispute policies. | Recommend 13+. |
| **Advertising (AdMob)** | Google Mobile Ads rewarded video ads. | **Requires Special Configuration** | To include children under 13, all ad SDKs must be enrolled in Google Play's **Families Self-Certified Ads SDK Program**, interest-based tracking must be disabled (`tagForChildDirectedTreatment`), and ad categories must be heavily restricted. | Recommend 13+. |
| **Account Creation & OAuth** | Optional Google / Facebook sign-in for cloud sync. | **Moderate** | COPPA restrictions apply to persistent cloud identifiers for children under 13 without verifiable parental consent. | Recommend 13+. |

---

## 2. Authoritative Target Audience Determination

### Official Decision: **General Audience — Ages 13 and Older**

**Selected Target Age Groups in Play Console:**
- [ ] Under 5
- [ ] 6–8
- [ ] 9–12
- [x] **13–15**
- [x] **16–17**
- [x] **18 and over**

### Rationale:
1. **Advertising & Privacy Simplicity:** Designating the app for users aged 13 and older ensures complete compliance without requiring child-directed ad tagging or COPPA parental consent mechanisms.
2. **Competitive Matchmaking:** Real-time multiplayer racing against live online opponents is appropriate for teens and adults.
3. **Financial Protection:** Auto-renewing subscriptions are targeted at mature players capable of managing their own Google Play accounts.

---

## 3. Could Zynpath Unintentionally Appeal to Children? ("Neutral Age Screen")
- During the Play Console Target Audience questionnaire, Google asks: *"Could your app unintentionally appeal to children?"*
- **Assessment:** While Zynpath features vibrant, clean visual styling (Midnight Navy and luminescent cyan paths), it does not feature cartoon animal mascots, nursery rhymes, or child-directed character IP. The game's aesthetic is that of a sleek, minimalist brain puzzle (similar to Sudoku, Nonograms, or Flow Free).
- **Play Console Selection:** Select **"No"** to unintentional appeal, or if Google requests it, implement a neutral age screen in future releases.
