# TDS → 웹 적응 규칙

> TDS는 모바일(React Native/웹뷰) 기준이므로 웹 프로젝트에 적용 시 아래 변환 규칙을 따른다.

---

## 1. Typography 매핑

| TDS 토큰 | 모바일 크기 | 웹 대응 | Tailwind 클래스 |
|----------|------------|---------|----------------|
| T1 (30px) | Display | fluid 36→56px | `text-fluid-display` |
| T2 (26px) | H1 | fluid 28→36px | `text-fluid-h1` |
| T3 (22px) | H2 | fluid 24→30px | `text-fluid-h2` |
| T4 (20px) | H3 | fluid 20→24px | `text-fluid-h3` |
| T5 (17px) | Body | 16px | `text-base` |
| T6 (15px) | Body SM | 14px | `text-sm` |
| T7 (13px) | Caption | 12px | `text-xs` |

**규칙**: 모바일의 고정 크기 → 웹의 fluid clamp()로 변환. 반응형 대응을 위해 breakpoint 대신 clamp 사용.

---

## 2. 컴포넌트 variant 패턴 매핑

### TDS `variant: fill | weak` → 우리 프로젝트

| TDS variant | 우리 Button variant | 설명 |
|-------------|-------------------|------|
| `fill` + `color: primary` | `primary` | 주요 액션 (파란색/인디고) |
| `fill` + `color: danger` | `danger` | 위험 액션 (빨간색) |
| `weak` + `color: primary` | `secondary` | 보조 액션 (반투명 배경) |
| `weak` + `color: light` | `ghost` | 최소 스타일 (배경 없음) |

### TDS Badge `color` → 우리 Badge `variant`

| TDS color | 우리 variant | 용도 |
|-----------|-------------|------|
| `blue` | `primary` | 기본 강조 |
| `green` | `success` | 성공/완료 |
| `red` | `danger` | 에러/긴급 |
| `yellow` | `warning` | 경고 |
| `elephant` (grey) | `default` | 중립/기본 |

---

## 3. 모바일 전용 컴포넌트 → 웹 대체

| TDS 모바일 | 웹 대체 패턴 | 비고 |
|-----------|-------------|------|
| BottomSheet | **SlidePanel** (우측) 또는 **Modal** | 웹에서는 하단 시트보다 사이드 패널이 자연스러움 |
| BottomCTA | **Fixed Footer** 또는 **inline Button** | 웹에서는 하단 고정 CTA가 덜 일반적 |
| Keypad | **native `<input type="number">`** | 웹은 OS 키보드 사용 |
| Highlight | **Tooltip** 또는 **Popover** | 온보딩 투어에만 제한적 사용 |
| NativeApp bridges | 해당 없음 | `showHighlight` 등 네이티브 브릿지 무시 |

---

## 4. 현재 shared/ui ↔ TDS 정렬 상태

| 컴포넌트 | 현재 상태 | TDS 정렬 필요 사항 |
|----------|---------|-------------------|
| **Button** | variant: primary/secondary/ghost/danger, size: sm/md/lg | TDS color prop 추가 고려. 기존 variant 유지 가능 |
| **Badge** | variant: default/primary/success/warning/danger | TDS fill/weak 패턴 추가, color prop 분리 고려 |
| **Input** | label/error props | TDS TextField variant(box/line) 참고. 현재는 box만 |
| **Modal** | isOpen/onClose/title/children | TDS Modal.Overlay/Content 분리 패턴 참고 |
| **Card** | padding: sm/md/lg | 현재 충분. TDS에 직접 대응 없음 |
| **Avatar** | src/name/size | TDS에 직접 대응 없음. 현재 충분 |
| **Spinner** | size: sm/md/lg | TDS Loader의 type(primary/dark/light) 추가 고려 |

### 신규 필요 컴포넌트 (Sprint 2)

| 컴포넌트 | TDS 참조 | 용도 |
|----------|---------|------|
| **Select/Dropdown** | TDS Menu (Trigger + DropdownItem) | 상태/담당자/우선순위 선택 |
| **Textarea** | TDS TextArea (TextField 확장) | 이슈 설명 |
| **DatePicker** | TDS에 없음 (자체 구현 또는 라이브러리) | 기한 선택 |
| **SlidePanel** | TDS BottomSheet → 웹 우측 패널 변환 | 이슈 상세 |
| **Toast** | TDS Toast (position/duration/aria-live) | 피드백 메시지 |
| **Tab** | TDS Tab (size/fluid/onChange) | 워크스페이스 사이드바 탭 |

---

## 5. forwardRef 정책

TDS 패턴에 따라 모든 shared/ui 컴포넌트는 `forwardRef`를 사용해야 한다.

**현재 미적용**: Badge, Modal, Avatar(부분), Spinner(부분)
**조치**: 다음 리팩터링 시 forwardRef 추가

---

## 6. 접근성(a11y) 최소 요구사항

TDS에서 명시하는 공통 접근성 규칙:

| 컴포넌트 | 필수 a11y |
|----------|-----------|
| Button (아이콘만) | `aria-label` 필수 |
| Checkbox | `aria-label` 필수 ("체크박스" 단어 중복 금지) |
| Switch | `aria-label` 필수 (상태 용어 제외) |
| IconButton | `aria-label` 필수 |
| Tab | `ariaLabel` (불명확한 라벨 시, "탭" 단어 금지) |
| Toast | `aria-live="polite"` (기본) 또는 `"assertive"` (긴급) |
| ListFooter | `aria-label` 필수 |
| Rating | `role="slider"` + aria-value 자동 |
