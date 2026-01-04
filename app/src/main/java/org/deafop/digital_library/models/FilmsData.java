package org.deafop.digital_library.models;

import org.deafop.digital_library.R;

public class FilmsData {
    public static Integer[] drawableArray;
    public static Integer[] id_ = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15};
    public static String[] nameArray = {"City Girl S1E1: Village Life", "City Girl S1E2: Journey to The City", "City Girl S1E3: The City Experience", "City Girl S1E4: The Going Gets Tough", "City Girl S1E5: After Party", "City Girl S1E6: Light at The End of The Tunnel", "City Girl S1E7: Finding Love", "City Girl S1E8: Happily Ever After","City Girl Sn2 Ep1","City Girl Sn2 Ep2","City Girl Sn2 Ep3","City Girl Sn2 Ep4","City Girl Sn2 Ep5","City Girl Sn2 Ep6", "Silent Cry", "Poem"};

    static {
        Integer valueOf = R.drawable.ep8;
        drawableArray = new Integer[]{R.drawable.ep1, R.drawable.ep2, R.drawable.ep3, R.drawable.ep4, R.drawable.ep5, R.drawable.ep6, R.drawable.ep7, R.drawable.ep8,R.drawable.s21,R.drawable.s22,R.drawable.s23,R.drawable.s24,R.drawable.s25,R.drawable.s26, R.drawable.silent, R.drawable.poem};
    }
}
