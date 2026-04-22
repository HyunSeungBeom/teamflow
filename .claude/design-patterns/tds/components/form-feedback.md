# TDS 폼/피드백 컴포넌트 (12개)

> 출처: https://tossmini-docs.toss.im/tds-mobile/components/
> TextField, TextArea, SplitTextField, SearchField, Modal, AlertDialog, ConfirmDialog, BottomSheet, Toast, Tab, SegmentedControl

---

## TextField
> 사용자로부터 입력을 받는 가장 기본적인 UI 요소

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| variant | `"box"` \| `"line"` \| `"big"` \| `"hero"` | - | 디자인 스타일 |
| label | `string` | - | 상단 라벨 |
| labelOption | `"appear"` \| `"sustain"` | `"appear"` | 라벨 표시 방식 |
| help | `ReactNode` | - | 하단 도움말 |
| hasError | `boolean` | `false` | 에러 상태 |
| disabled | `boolean` | `false` | 비활성화 |
| prefix | `string` | - | 입력 앞 텍스트 |
| suffix | `string` | - | 입력 뒤 텍스트 |
| placeholder | `string` | - | 플레이스홀더 |

### Variants
- **box**: 기본 사각형, 깔끔하고 간결
- **line**: 하단 선만 있는 미니멀
- **big**: 텍스트 강조 확대
- **hero**: 대형 눈에 띄는 스타일

### Sub-Components
- **TextField.Clearable**: 삭제 버튼 (`onClear`)
- **TextField.Password**: 비밀번호 표시/숨김 토글
- **TextField.Button**: 우측 버튼 (기본: 화살표 아이콘)

---

## TextArea
> 여러 줄 텍스트 입력 (TextField 확장)

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| minHeight | `string \| number` | - | 최소 높이 (자동 확장) |
| height | `string \| number` | - | 고정 높이 |

TextField의 모든 props 상속 (prefix, suffix, right 제외)

---

## SearchField
> 검색 입력창 (상단 고정 + 삭제 기능)

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| fixed | `boolean` | `false` | 상단 고정 |
| takeSpace | `boolean` | `true` | 레이아웃 시프트 방지 |
| onDeleteClick | `() => void` | - | 삭제 아이콘 클릭 콜백 |
| placeholder | `string` | - | 힌트 텍스트 |

---

## Modal
> 중요한 내용 표시 또는 즉각적 상호작용이 필요할 때 사용하는 오버레이

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| open | `boolean` | - | 열림/닫힘 |
| onOpenChange | `(open: boolean) => void` | - | 상태 변경 콜백 |
| onExited | `() => void` | - | 닫힘 애니메이션 완료 콜백 |

### Sub-Components
- **Modal.Overlay**: 배경 오버레이 (`onClick` 지원)
- **Modal.Content**: 콘텐츠 컨테이너

### Accessibility
- `aria-hidden`: 배경 콘텐츠 숨김
- `tabIndex={0}`: 자동 포커스
- `role="button"`: 오버레이 클릭 가능성 전달

---

## AlertDialog
> 중요 정보 전달 + 단일 확인 버튼

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| open | `boolean` | - | 표시 제어 |
| title | `ReactNode` | - | 제목 |
| description | `ReactNode` | - | 보충 설명 (선택) |
| alertButton | `ReactNode` | - | 확인 버튼 |
| closeOnDimmerClick | `boolean` | `true` | 배경 클릭 닫기 |
| onClose | `() => void` | - | (필수) 닫힘 콜백 |

### Sub-Components
- **AlertDialog.Title**: h3, t4 타이포, bold
- **AlertDialog.Description**: h3, t6 타이포, medium
- **AlertDialog.AlertButton**: 텍스트 버튼

---

## ConfirmDialog
> 확인 + 취소 두 버튼으로 명확한 선택 유도

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| open | `boolean` | - | 표시 제어 |
| title | `ReactNode` | - | 제목 |
| description | `ReactNode` | - | 보충 설명 (선택) |
| cancelButton | `ReactNode` | - | 취소 버튼 |
| confirmButton | `ReactNode` | - | 확인 버튼 |
| onClose | `() => void` | - | 닫힘 콜백 |

### Guidelines
- 버튼 텍스트 짧으면 가로 배치, 길면 세로 스택
- `closeOnDimmerClick={false}` 시 wiggle 애니메이션

---

## BottomSheet
> 하단에서 슬라이드 올라오는 패널

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| open | `boolean` | - | 표시 여부 |
| onClose | `() => void` | - | 닫힘 콜백 |
| header | `ReactNode` | - | 제목 섹션 |
| cta | `ReactNode` | - | 하단 CTA 버튼 |
| children | `ReactNode` | - | 본문 |
| expandBottomSheet | `boolean` | `false` | 풀스크린 확장 |
| hasTextField | `boolean` | `false` | 키보드 위로 이동 |

### Sub-Components
- **BottomSheet.Header/HeaderDescription/CTA/DoubleCTA/Select**

---

## Toast
> 작업 완료/이벤트 피드백 (짧은 시간 표시 후 자동 사라짐)

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| open | `boolean` | (필수) | 표시 제어 |
| position | `"top"` \| `"bottom"` | (필수) | 위치 |
| text | `string` | (필수) | 메시지 |
| duration | `number` | `3000` | 자동 닫힘 (ms) |
| button | `ReactNode` | - | 액션 버튼 (bottom 전용) |
| aria-live | `"assertive"` \| `"polite"` | `"polite"` | 스크린리더 우선순위 |

### Sub-Components
- **Toast.Button/Icon/Lottie**

---

## Tab
> 여러 콘텐츠를 한 화면에서 전환

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| children | `ReactNode` | (필수) | Tab.Item들 |
| onChange | `(index, key?) => void` | (필수) | 탭 변경 콜백 |
| size | `"large"` \| `"small"` | `"large"` | 크기 |
| fluid | `boolean` | `false` | 글자수 기반 너비 |

### Sub-Components
- **Tab.Item**: `selected`, `redBean` (알림 점)

### Accessibility
- `role="tablist"`, `role="tab"`, `aria-selected` 자동

---

## SegmentedControl
> 여러 선택지 중 하나를 선택 (Radio 역할)

### Props
| Prop | Type | Default | Description |
|------|------|---------|-------------|
| size | `"small"` \| `"large"` | `"small"` | 크기 |
| alignment | `"fixed"` \| `"fluid"` | `"fixed"` | 너비 방식 |
| value | `string` | - | 제어 모드 |
| defaultValue | `string` | - | 비제어 모드 |
| onChange | `(v: string) => void` | - | 변경 콜백 |

### Accessibility
- `role="radiogroup"` + `role="radio"`, `aria-checked` 자동
