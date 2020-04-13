package com.dsy.blog.modelEntity;

import lombok.*;

/**
 * Created on 2020/4/9
 * Package com.dsy.blog.modelEntity
 *
 * @author dsy
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TagTops implements Comparable<TagTops> {

    private Integer tagId;
    private String name;
    private Integer BlogNumber;

    @Override
    public int compareTo(TagTops o) {
        return o.BlogNumber - this.BlogNumber;
    }
}
