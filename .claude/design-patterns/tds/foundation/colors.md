# TDS Colors (토스 디자인 시스템 색상 체계)

> 출처: https://tossmini-docs.toss.im/tds-mobile/foundation/colors/
> 개발자와 디자이너가 통일된 색상 이름을 사용하도록 하여 일관된 UI 구현을 지원한다.

---

## 색상 팔레트 (50~900 스케일)

| 계열 | 50 | 100 | 200 | 300 | 400 | 500 | 600 | 700 | 800 | 900 |
|------|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|
| **Grey** | #f9fafb | #f2f4f6 | #e5e8eb | #d1d6db | #b0b8c1 | #8b95a1 | #6b7684 | #4e5968 | #333d4b | #191f28 |
| **Blue** | #e8f3ff | #c9e2ff | #90c2ff | #64a8ff | #4593fc | #3182f6 | #2272eb | #1b64da | #0f50b8 | #194aa6 |
| **Red** | #ffeeee | #ffd4d4 | #ffaaaa | #ff7777 | #ff5555 | #ff3333 | #f03030 | #e02020 | #c01818 | #a51926 |
| **Orange** | #fff3e0 | #ffe0b2 | #ffcc80 | #ffb74d | #ffa726 | #ff9100 | #fb8c00 | #f57c00 | #ef6c00 | #e45600 |
| **Yellow** | #fff9e7 | #fff2cc | #ffe699 | #ffd966 | #ffcc33 | #ffbf00 | #f5b800 | #e6a900 | #d99c00 | #dd7d02 |
| **Green** | #f0faf6 | #d8f0e7 | #a3dcbf | #6dc59b | #3cb878 | #1bab6b | #10945a | #0a7e4d | #057840 | #027648 |
| **Teal** | #edf8f8 | #d4efef | #a6e0df | #77cece | #4bbcbc | #21a8a8 | #15918e | #0f7c7a | #0a6a68 | #076565 |
| **Purple** | #f9f0fc | #f0dcf5 | #dfb5ea | #ca8ddb | #b96bcd | #a64ec2 | #923ead | #7e3098 | #6c2485 | #65237b |

## 특수 색상

### Grey Opacity (투명도 기반)
- 배경/오버레이에서 사용
- 50~900 범위, 각 단계별 투명도 적용

### 배경색
| 토큰 | 용도 |
|------|------|
| `background` | 기본 배경 |
| `greyBackground` | 회색 배경 |
| `layeredBackground` | 레이어드 배경 |
| `floatedBackground` | 떠있는 요소 배경 |

---

## 웹 적용 시 참고

- TDS 원본은 `@toss/tds-colors` 패키지에서 제공
- 웹 프로젝트에서는 Tailwind CSS @theme 변수로 매핑하여 사용
- Grey 계열은 Tailwind 기본 gray 스케일과 유사하나 미묘한 차이 있음
