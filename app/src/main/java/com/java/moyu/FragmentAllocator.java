package com.java.moyu;

class FragmentAllocator {
    private IndexFragment indexFragment;

    BasicFragment getDefault() {
        return getIndexFragment();
    }

    IndexFragment getIndexFragment() {
        if (indexFragment == null) {
            indexFragment = new IndexFragment();
        }
        return indexFragment;
    }
}
