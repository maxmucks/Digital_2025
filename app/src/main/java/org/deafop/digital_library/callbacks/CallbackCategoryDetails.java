package org.deafop.digital_library.callbacks;

import org.deafop.digital_library.models.Category;
import org.deafop.digital_library.models.Video;

import java.util.ArrayList;
import java.util.List;

public class CallbackCategoryDetails {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public Category category = null;
    public List<Video> posts = new ArrayList<>();

}
