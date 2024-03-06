func write(string s) = _code("int 0x04000000");

func println(string msg) {
    write(msg);
    write("\n");
}

func<int> main(int argc, long argv) {
    int h = 1 + 2 + 3 + 4 + 5;

    return 0;
}