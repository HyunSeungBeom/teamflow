# TDS 기본 컴포넌트 (10개)

> 출처: https://tossmini-docs.toss.im/tds-mobile/components/
> Button, Badge, Checkbox, Switch, IconButton, TextButton, Loader, Skeleton, Tooltip, Highlight

---

## Button
> 사용자가 어떤 액션을 트리거하거나 이벤트를 실행할 때 사용

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| as | `"button"` \| `"a"` | `"button"` | 태그 전환 |
| color | `"primary"` \| `"danger"` \| `"light"` \| `"dark"` | `"primary"` | 버튼 색상 |
| variant | `"fill"` \| `"weak"` | `"fill"` | 채도/투명도 조절 |
| display | `"inline"` \| `"block"` \| `"full"` | `"inline"` | 너비 및 레이아웃 |
| size | `"small"` \| `"medium"` \| `"large"` \| `"xlarge"` | `"xlarge"` | 크기 |
| loading | `boolean` | - | 로딩 스피너 표시 |
| disabled | `boolean` | - | 비활성화 |
| type | `"button"` \| `"submit"` \| `"reset"` | - | HTML type |

### Variants
- **fill**: 높은 채도, 시각적으로 강렬. 주요 액션용
- **weak**: 낮은 채도, 반투명 배경. 보조 액션용

### Accessibility
- 네이티브 시맨틱 HTML → 스크린 리더가 "버튼"으로 인식
- `aria-disabled`, `aria-busy` 자동 적용
- 아이콘만 있는 버튼은 `aria-label` 필수

### CSS Variables
`--button-color`, `--button-background-color`, `--button-pressed-background-color`, `--button-pressed-opacity`, `--button-disabled-opacity-color`, `--button-loader-color`

---

## Badge
> 항목의 상태를 빠르게 인식할 수 있도록 강조

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| variant | `"fill"` \| `"weak"` | (필수) | 채도/투명도 |
| size | `"xsmall"` \| `"small"` \| `"medium"` \| `"large"` | (필수) | 크기 |
| color | `"blue"` \| `"teal"` \| `"green"` \| `"red"` \| `"yellow"` \| `"elephant"` | (필수) | 색상 |

### Variants
- **fill**: 높은 채도, 주요 항목 강조
- **weak**: 낮은 채도, 보조 상태 표시

---

## Checkbox
> 하나 이상의 항목을 선택할 때 사용

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| inputType | `"checkbox"` \| `"radio"` | `"checkbox"` | input type |
| size | `number` | `24` | 크기 (px) |
| checked | `boolean` | - | 외부 제어 |
| onCheckedChange | `(checked: boolean) => void` | - | 변경 콜백 |
| defaultChecked | `boolean` | - | 내부 제어 초기값 |
| disabled | `boolean` | - | 비활성화 |
| aria-label | `string` | - | (필수) 접근성 레이블 |

### Sub-Components
- **Checkbox.Circle**: 원형 배경 + 체크 아이콘
- **Checkbox.Line**: 체크 아이콘만

### Accessibility
- `role="checkbox"`, `tabindex="0"`, `aria-checked` 자동 적용
- `aria-label` 필수 ("체크박스" 단어 중복 금지)

---

## Switch
> 두 가지 상태(on/off) 간 전환 토글

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| checked | `boolean` | (필수) | on/off 상태 |
| disabled | `boolean` | `false` | 비활성화 |
| onChange | `(event, checked) => void` | - | 토글 콜백 |

### Accessibility
- `role="switch"`, `aria-checked`, `aria-disabled` 자동
- `aria-label` 필수 (상태 용어 "켜짐/꺼짐" 제외)

---

## IconButton
> 아이콘으로 액션을 실행하는 컴포넌트

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| aria-label | `string` | - | (필수) 액션 설명 |
| variant | `"fill"` \| `"clear"` \| `"border"` | `"clear"` | 시각적 스타일 |
| iconSize | `number` | `24` | 아이콘 크기 (px) |
| bgColor | `string` | `greyOpacity100` | 배경 색상 |

### Variants
- **clear**: 배경 없이 아이콘만 (기본)
- **fill**: 색상 배경으로 강조
- **border**: 테두리 스타일

---

## TextButton
> 텍스트 기반 액션 컴포넌트

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| size | `"xsmall"` ~ `"xxlarge"` | (필수) | 크기 |
| variant | `"arrow"` \| `"underline"` \| `"clear"` | `"clear"` | 스타일 |
| disabled | `boolean` | - | 비활성화 (opacity: 0.38) |

---

## Loader
> 로딩 중 시각적 피드백

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| size | `"small"` \| `"medium"` \| `"large"` | `"medium"` | 크기 |
| type | `"primary"` \| `"dark"` \| `"light"` | `"primary"` | 색상 스킴 |
| label | `string` | - | 하단 텍스트 |

---

## Skeleton
> 로딩 중 콘텐츠 레이아웃 플레이스홀더

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| pattern | preset string | `"topList"` | 프리셋 레이아웃 |
| custom | `Array` | - | 커스텀 레이아웃 |
| background | `"white"` \| `"grey"` \| `"greyOpacity100"` | `"grey"` | 배경색 |

### Patterns
topList, topListWithIcon, amountTopList, subtitleList, listOnly, cardOnly 등

---

## Tooltip
> 특정 요소에 추가 정보를 제공

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| size | `"small"` \| `"medium"` \| `"large"` | `"medium"` | 크기 |
| placement | `"top"` \| `"bottom"` | `"bottom"` | 위치 |
| message | `ReactNode` | - | 툴팁 내용 |
| dismissible | `boolean` | `false` | 외부 클릭/ESC로 닫기 |
| autoFlip | `boolean` | `false` | 뷰포트 밖 시 자동 반전 |

---

## Highlight
> 특정 영역을 강조 (주변을 어둡게 처리)

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| open | `boolean` | (필수) | 표시 여부 |
| padding | `number` | `0` | 강조 영역 여백 (px) |
| message | `string \| function` | - | 설명 텍스트 |
| onClick | `() => void` | - | 강조 외부 클릭 콜백 |
