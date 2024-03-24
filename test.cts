module main;

import io;

struct Vector {
    int length;
    int capacity;
    int[] data;

    Vector() {
        this.length = 0;
        this.capacity = 10;
        this.data = new int[10];
    }

    func<void> add(int i) {
        if (this.length >= this.capacity) {
            this.capacity = this.capacity * 2;
            int[] newData = new int[this.capacity];

            for (int j = 0; i < this.length; j = j + 1) {
                newData[j] = this.data[j];
            }

            delete this.data;
            this.data = newData;
        }

        this.data[this.length] = i;
        this.length = this.length + 1;
    }
}

int x = 69;

func<int> main(int argc, string[] argv) {
    Vector vec = Vector();

    vec.add(x);

    io.println(x);

    return 0;
}