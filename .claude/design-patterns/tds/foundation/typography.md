# TDS Typography (토스 디자인 시스템 타이포그래피)

> 출처: https://tossmini-docs.toss.im/tds-mobile/foundation/typography/
> 계층적 타이포그래피 토큰(T1~T7, ST1~ST13)을 사용한다. 하드코딩된 값 대신 토큰을 사용해야 접근성 설정에 대응 가능.

---

## 기본 스케일 (100% 기준)

| 토큰 | Font Size | Line Height | 용도 |
|------|-----------|-------------|------|
| T1 | 30px | 40px | 매우 큰 제목 |
| T2 | 26px | 35px | 큰 제목 |
| T3 | 22px | 31px | 일반 제목 |
| T4 | 20px | 29px | 작은 제목 |
| T5 | 17px | 25.5px | 본문 |
| T6 | 15px | 22.5px | 작은 본문 |
| T7 | 13px | 19.5px | 선택적 읽기 텍스트 |

### 서브타입 (ST1~ST13)
- 메인 타이포그래피 레벨 사이의 중간 크기
- 세밀한 크기 조절이 필요한 경우 사용

## Font Weight

| Weight | 값 | 용도 |
|--------|-----|------|
| Light | 300 | 장식적 텍스트 |
| Regular | 400 | 기본 본문 |
| Medium | 500 | 강조 본문 |
| SemiBold | 600 | 부제목 |
| Bold | 700 | 제목, 강조 |

## 접근성 스케일링

### iOS
- Large: 100%, xLarge: 110%, xxLarge: 120%, xxxLarge: 135%
- A11y_Medium ~ A11y_xxxLarge: 최대 310%

### Android
- 공식 기반: `BaseSize × NN% × 0.01` (100% 이상 어떤 값이든 지원)

---

## 웹 적용 시 매핑

| TDS 토큰 | 웹 fluid 대응 | 비고 |
|----------|---------------|------|
| T1 (30px) | `text-fluid-display` (36→56px) | 히어로/디스플레이 |
| T2 (26px) | `text-fluid-h1` (28→36px) | 페이지 제목 |
| T3 (22px) | `text-fluid-h2` (24→30px) | 섹션 제목 |
| T4 (20px) | `text-fluid-h3` (20→24px) | 서브 제목 |
| T5 (17px) | `text-base` (16px) | 본문 |
| T6 (15px) | `text-sm` (14px) | 보조 텍스트 |
| T7 (13px) | `text-xs` (12px) | 캡션/오버라인 |
