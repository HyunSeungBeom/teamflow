# TDS 레이아웃/리스트 ��포넌트 (16개)

> 출처: https://tossmini-docs.toss.im/tds-mobile/components/
> ListRow, BoardRow, TableRow, ListHeader, ListFooter, ProgressBar, ProgressStepper, Rating, Result, Menu, Paragraph, Border, GridList, BottomCTA.Single, BottomCTA.Double

---

## ListRow
> 리스트 아이템의 3-zone 레이아웃 (left / contents / right)

### Sub-Components
| Sub | 용도 |
|-----|------|
| ListRow.AssetIcon | 좌측 아이콘 |
| ListRow.IconButton | 좌측 버튼형 아이콘 |
| ListRow.AssetImage | 좌측 이미지 |
| ListRow.AssetLottie | 좌측 Lottie 애니메이션 |
| ListRow.AssetText | 좌측 텍스트 |
| ListRow.Texts | contents 영역 (title + description) |
| ListRow.Loader | 스켈레톤 로딩 UI |

### Guidelines
- 3-zone 구조: left(아이콘/이미지) + contents(텍스트) + right(액세서리)
- Loader로 스켈레톤 상태 지원

---

## BoardRow
> 아코디언 컴포넌트 (열기/닫기 제어)

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| open | `boolean` | - | 열림 상태 |
| onOpenChange | `(open) => void` | - | 상태 변경 콜백 |

### Sub-Components
- BoardRow.Prefix, BoardRow.ArrowIcon, BoardRow.Text

---

## TableRow
> 좌/우 데이터 표시

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| alignment | `"space-between"` \| `"left"` | - | 정렬 모드 |

---

## ListHeader
> 섹션 헤더 (제목 + 우측 액세서리)

### Title Variants
- **Paragraph**: 기본 텍스트
- **TextButton**: 클릭 가능한 텍스트
- **Selector**: 선택 가능한 드롭다운

---

## ListFooter
> 리스트 하단 (테두리 + 아이콘/텍스트)

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| aria-label | `string` | - | (필수) 접근성 레이블 |

---

## ProgressBar
> 진행률 표시 (0.0 ~ 1.0)

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| value | `number` | (필수) | 0.0~1.0 진행률 |
| size | `"light"` \| `"normal"` \| `"bold"` | `"normal"` | 굵기 |
| color | `string` | - | 색상 오버라이드 |

---

## ProgressStepper
> 단계별 진행 표시

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| totalStep | `number` | (필수) | 총 단계 수 |
| currentStep | `number` | (필수) | 현재 단계 |
| variant | `"compact"` \| `"icon"` | `"compact"` | 스타일 |
| checkForFinish | `boolean` | `false` | 완료 단계 체크 아이콘 |

---

## Rating
> 별점 평가 (인터랙티브 + 읽기 전용)

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| count | `number` | `5` | 별 개수 |
| value | `number` | - | 현재 값 |
| size | `"small"` \| `"medium"` \| `"large"` \| `"xlarge"` \| `"xxlarge"` | `"medium"` | 크기 |

### Read-Only Variants
- **plain**: 채워진 별만
- **withValue**: 별 + 숫자 값
- **withValueAndCount**: 별 + 값 + 평가 수

### Accessibility
- `role="slider"`, `aria-valuemin/max/now`, `aria-label` 자동

---

## Result
> 결과 페이지 (피규어 + 제목 + 설명 + 버튼)

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| figure | `ReactNode` | - | 상단 아이콘/이미지 |
| title | `ReactNode` | - | 결과 제목 (자동 `<h5>`) |
| description | `ReactNode` | - | 보충 설명 |
| cta | `ReactNode` | - | CTA 버튼 |

---

## Menu
> 드롭다운 메뉴

### Sub-Components
- **Menu.Trigger**: 12가지 placement 지원
- **Menu.DropdownItem**: 일반 메뉴 항목
- **Menu.DropdownCheckItem**: 체크박스 메뉴 항목

### Placement Options
top, top-start, top-end, bottom, bottom-start, bottom-end, left, left-start, left-end, right, right-start, right-end

---

## Paragraph
> 텍스트 표시 + 인라인 요소 조합

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| typography | `"t1"` ~ `"st13"` | (필수) | 텍스트 크기 |
| fontWeight | `"regular"` \| `"medium"` \| `"semibold"` \| `"bold"` | `"regular"` | 굵기 |
| color | `string` | - | 색상 |
| ellipsisAfterLines | `number` | - | 말줄임 처리 |

### Sub-Components
- **Paragraph.Text/Badge/Link/Icon**

---

## Border
> 구분선

### Variants
- **full**: 전체 너비
- **padding24**: 좌우 24px 패딩
- **height16**: 높이 16px (두꺼운 구분)

---

## GridList
> 그리드 형태 아이템 배치

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| column | `1` \| `2` \| `3` | `3` | 열 개수 |

### Item Props
- **image**: 이미지 ReactNode
- **children**: 하단 텍스트

---

## BottomCTA.Single
> 하단 고정 단일 버튼 CTA

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| children | `ReactNode` | (필수) | 버튼 콘텐츠 |
| fixed | `boolean` | - | 하단 고정 |
| background | `"default"` \| `"none"` | `"default"` | 배경 |
| showAfterDelay | `{ animation, delay }` | - | 등장 애니메이션 |
| hideOnScroll | `boolean` | `false` | 스크롤 시 숨김 |

---

## BottomCTA.Double
> 하단 고정 이중 버튼 CTA

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| leftButton | `ReactNode` | (필수) | 왼쪽 버튼 |
| rightButton | `ReactNode` | (필수) | 오른쪽 버튼 |

BottomCTA.Single과 동일한 컨테이너 props 지원
