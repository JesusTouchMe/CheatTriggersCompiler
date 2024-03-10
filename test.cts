struct Vec2 {
    int x;
    int y;
}

native func<void> test(int thang);

func printVec2(Vec2 vec) {
    test(vec.x);
    test(vec.y);
}

func<int> main(int argc, long argv) {
    Vec2 a;
    Vec2 b;
    a.x = 1;
    a.y = 2;
    b.x = 3;
    b.y = 4;

    printVec2(a);
    printVec2(b);

    return 0;
}