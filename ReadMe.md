# ADS04 Android

## 수업 내용
- rxJava에서 map, flatMap, zip을 학습

## Code Review

### MainActivity

```Java
public class MainActivity extends AppCompatActivity {

    RecyclerView recyler;
    CustomAdapter adapter;
    List<String> months = new ArrayList<>();
    Observable<String> observableZip;
    String monthString[];
    Observable<String> observable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyler = findViewById(R.id.recycler);
        adapter = new CustomAdapter();
        recyler.setAdapter(adapter);
        recyler.setLayoutManager(new LinearLayoutManager(this));

        DateFormatSymbols dfs = new DateFormatSymbols();
        monthString = dfs.getMonths();

        // 1. 발행자
        observable = Observable.create(
                e -> {
                    try {
                        for (String month : monthString) {
                            e.onNext(month);
                            Thread.sleep(1000);
                        }
                        e.onComplete();
                    } catch (Exception ex) {
                        throw ex;
                    }
                }
        );

        Observable<String> obs1 = Observable.create(
                e-> {
                    try {
                        Thread.sleep(1000);
                        e.onNext("Singer");
                        Thread.sleep(1000);
                        e.onNext("Athlete");
                        Thread.sleep(5000);
                        e.onNext("Rapper");
                        e.onComplete();
                    } catch (Exception ex) {
                        throw ex;
                    }
                });

        observableZip = Observable.zip(
                Observable.just("BeWhy", "Curry", "Zico"),
                obs1,
                (item1, item2) -> "job=".concat(item2).concat(", name=").concat(item1)
        );
    }

    /*
        map 은 데이터 자체를 변형 할 수 있다
     */
    public void doMap(View view) {
        months.clear();
        observable
                .subscribeOn(Schedulers.io())              // 옵저버블의 thread를 지정
                .observeOn(AndroidSchedulers.mainThread()) // 옵저버의 thread를 지정
                .filter(str -> str.equals("March") ? false : true)
                .map(str -> {
                    if (str.equals("April")) {
                        return "[" + str + "]";
                    } else {
                        return str;
                    }
                })
                .subscribe(
                        str -> {
                            months.add(str);
                            adapter.setDataAndRefresh(months);
                        }
                );
    }

    /*
        flatMap은 아이템을 여러개로 분리할 수 있다.
     */
    public void doFlatmap(View view) {
        months.clear();
        observable
                .subscribeOn(Schedulers.io())              // 옵저버블의 thread를 지정
                .observeOn(AndroidSchedulers.mainThread()) // 옵저버의 thread를 지정
                .filter(str -> str.equals("March") ? false : true)
                .flatMap(item -> {
                    return Observable.fromArray(new String[]{"name:" + item, "[" + item + "]"});
                })
                .subscribe(
                        str -> {
                            months.add(str);
                            adapter.setDataAndRefresh(months);
                        }
                );
    }

    /*
        복수개의 옵저버블을 하나로 묶어준다
     */
    public void doZip(View view) {
        months.clear();
        observableZip
                .subscribeOn(Schedulers.io())              // 옵저버블의 thread를 지정
                .observeOn(AndroidSchedulers.mainThread()) // 옵저버의 thread를 지정
                .subscribe(
                        str -> {
                            months.add(str);
                            adapter.setDataAndRefresh(months);
                        }
                );
    }
}
```


## 보충설명

### Map 

![map](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0CByE94any3Vkr84rlfqrpoVJYM5QdCWs1jIxDqCqPaWJXRaQ)

- map은 한 데이터를 다른 데이터로 바꾸는 오퍼레이터
- 원본의 데이터를 변경하지 않고 새로운 스트림을 만들어 냄

- 예제 코드

```Java
simpleObservable
    .map(new Func1<String, String>() {
        @Override
        public String call(String text) {
            return text.toUpperCase();
        }
    })
    .subscribe(new Action1<String>() {
        @Override
        public void call(String text) {
            ((TextView) findViewById(R.id.textView)).setText(text);
        }
    })
    // 문자열을 대문자로 바꾸는 코드
```

### flatMap

![flatMap](https://farm8.staticflickr.com/7567/26230104214_635e66ac0b_z.jpg)

- Observable에서 발행한 아이템을 다른 Observable로 만들며, 만들어진 Observable에서 아이템을 발행

### filter

![filter](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6LDpncBpZZsLusC09Ssv0GUgxFTPyY1B5sR1GI6QmzUKqsV75)

- 결과의 취사선택이 가능

### zip

![Zip](http://www.pineappslab.com/img/zip.png)

- Observable을 합성하는 경우에 쓰임.
- 네트워크 작업으로 사용자의 프로필과 프로필 이미지를 동시에 요청하고, 그 결과를 합성해서 화면에 표현해준다거나 하는 형태의 작업이 필요한 경우 zip() 유용하게 사용


### 출처

- 출처 : http://gaemi.github.io/android/2015/05/20/RxJava-with-Android-1-RxJava-%EC%82%AC%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0.html
- 출처: https://pluu.github.io/blog/rx/2015/04/29/rxjava/
- [reactiveX](http://reactivex.io/)

## TODO

- 위에서 학습한 map, flatmap, zip말고 다른 부분도 공부해보기. [카테고리별Operator](http://reactivex.io/documentation/operators.html)

## Retrospect

- TODO에 적어놓은 것 처럼, category별로 정리된 Operator의 기능들을 공부해봐야 할 것같다. 완벽하게는 아니더라도, 어떤 기능이 있는 지 정도는 공부함으로써, 추후에 필요하면 빠르게 찾을 수 있도록 한다. 

## Output

- 생략