module io;

func loadLibrary(string path) = _code(
    "int 0x08000000"
);

@constructor
func<void> stdIOInit() {
    loadLibrary("C:/Users/Jannik/IdeaProjects/CTVMNative/build/libs/CTVMNative-0.1.1.jar");
}

native func<void> println(int msg);