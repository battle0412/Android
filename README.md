## Timer

### Timer 구성
1. 메인 타이머
  전체 시간을 나타내는 타이머
  모든 타이머는 시분초를 나타냄
2. 서브 타이머
  이전 구간과 차이를 나타내는 타이머
2. 시작 및 중지 버튼
  메인 및 구간기록 타이머 시작 및 일시정지
3. 구간 기록 및 초기화 버튼
  구간 기록 저장 및 초기화

### Timer 기술 설명

  스레드 1개만 사용하여 메인 및 구간기록 타이머 시작
  
  구간 기록 버튼
  
    스크롤 뷰에 구간기록 레이아웃 추가
    서브 타이머 값 초기화
    기록이 3개 이상인 경우 최소(<span style="color:#0000FF">파랑</span>), 최대(빨강) 표시
<span style="color:#ffd33d">파랑</span>
### Timer 사용법

  타이머 시작 후 구간기록 버튼을 눌러 기록 측정
  
<img src="https://user-images.githubusercontent.com/45412843/198030826-ce38c6f2-f44d-4516-ae86-f1fea8a27d9e.jpg" width="250" height="400">
