# TDS 디자인 패턴 사전

> 토스 디자인 시스템(TDS)을 기반으로 한 디자인 패턴 참조 문서.
> PM → Figma → Code 파이프라인에서 일관된 기준점으로 사용한다.

---

## 구조

```
.claude/design-patterns/tds/
├── README.md                    ← (이 파일) 인덱스
├── foundation/
│   ├── colors.md               ← 색상 스케일 (Grey~Purple 50-900)
│   └── typography.md           ← 타이포그래피 (T1~T7, ST1~ST13)
├── components/
│   ├── basic.md                ← 기본 컴포넌트 10개
│   ├── form-feedback.md        ← 폼/피드백 컴포넌트 12개
│   └── layout-list.md          ← 레이아웃/리스트 컴포넌트 16개
└── web-adaptation.md           ← TDS 모바일 → 웹 변환 규칙
```

---

## 컴포넌트 목록 (38개)

### 기본 (basic.md)
Button, Badge, Checkbox, Switch, IconButton, TextButton, Loader, Skeleton, Tooltip, Highlight

### 폼/피드백 (form-feedback.md)
TextField, TextArea, SplitTextField, SearchField, Modal, AlertDialog, ConfirmDialog, BottomSheet, Toast, Tab, SegmentedControl

### 레이아웃/리스트 (layout-list.md)
ListRow, BoardRow, TableRow, ListHeader, ListFooter, ProgressBar, ProgressStepper, Rating, Result, Menu, Paragraph, Border, GridList, BottomCTA.Single, BottomCTA.Double

---

## 커맨드에서 참조하는 방법

### `/front:design-system`
- `foundation/colors.md` → Figma Foundation Colors 카드 생성 기준
- `foundation/typography.md` → Figma Foundation Typography 카드 생성 기준
- `components/*.md` → Figma Components 페이지 컴포넌트 정의 기준

### `/front:design-page`
- 페이지 디자인 시 `components/*.md`에서 해당 컴포넌트의 variants/sizes/props 참조
- `web-adaptation.md`에서 TDS → 웹 변환 규칙 확인

### `/front:design-to-code`
- Figma 디자인 → 코드 변환 시 `web-adaptation.md`의 매핑 테이블 참조
- 기존 `shared/ui/` 컴포넌트와 TDS 정렬 상태 확인

---

## 핵심 패턴

### variant: fill | weak
TDS의 가장 핵심적인 이분법. 모든 컴포넌트에 적용:
- **fill**: 높은 채도, 시각적으로 강렬 → 주요 액션/상태
- **weak**: 낮은 채도, 부드러운 느낌 → 보조 액션/상태

### color prop
시맨틱 컬러를 별도 prop으로 분리:
- Button: `primary | danger | light | dark`
- Badge: `blue | teal | green | red | yellow | elephant`

### 접근성(a11y) 기본 탑재
아이콘만 있는 컴포넌트에는 `aria-label` 필수. 역할(role) 자동 적용.

---

## 출처
- https://tossmini-docs.toss.im/tds-mobile/
- 크롤링 날짜: 2026-04-21
