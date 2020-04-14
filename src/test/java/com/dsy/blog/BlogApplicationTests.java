package com.dsy.blog;

import com.dsy.blog.mapper.BlogMapper;
import com.dsy.blog.modelEntity.TagTops;
import com.dsy.blog.modelEntity.TypeTops;
import com.dsy.blog.po.Blog;
import com.dsy.blog.po.Comment;
import com.dsy.blog.po.Tag;
import com.dsy.blog.po.Type;
import com.dsy.blog.service.BlogService;
import com.dsy.blog.service.CommentService;
import com.dsy.blog.service.TagService;
import com.dsy.blog.service.TypeService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

@SpringBootTest
class BlogApplicationTests {

//    @Test
//    void contextLoads() {
//    }
//
//
//    @Autowired
//    private TypeService typeService;
//
//    @Autowired
//    private TagService tagService;
//
//    @Autowired
//    private BlogService blogService;
//
//    @Autowired
//    private CommentService commentService;
//
//
//    @Autowired
//    private BlogMapper blogMapper;
//
//
//
//
//    @Test
//    public void getTagByName(){
//        Tag tag = tagService.getTagByTagName("Redis");
//        System.out.println(tag);
//    }
//
//
//    @Test
//    public void getBlogsByPage(){
////        Type type = new Type();
////        type.setName("杜世银");
////        Blog blog = new Blog();
////        blog.setType(type);
////        PageHelper.startPage(1,10);
////        Page<Blog> blogs = blogService.findBlogByPage(blog);
////        PageInfo<Blog> pageInfo = new PageInfo<>(blogs);
////        System.out.println(blogs);
//    }
//
//
//    @Test
//    public void testOneToOne(){
//        List<Blog> blogList = blogMapper.findBlogAll();
//        System.out.println(blogList);
//    }
//
//    @Test
//    public void testFindBlogsByIds(){
//        String ids = "1,2";
//        List<Tag> tags = tagService.listTag(ids);
//        System.out.println(tags);
//    }
//
//
//    @Test
//    public void selectBlogByKeyWords(){
////        Page<Blog> blogs = blogService.selectBlogByKeyWords("条件", null, null);
////        System.out.println(blogs);
//    }
//
//    @Test
//    public void findBlogByKeyWords(){
////        List<Blog> blogList = blogMapper.findBlogByKeyWords("条件", "12", "1");
////        System.out.println(blogList);
//    }
//
//    @Test
//    public void findTagsByBlogId() {
//        String tags = blogService.findTagsByBlogId(8);
//        System.out.println(tags);
//    }
//
//    @Test
//    public void deleteBlog() {
//        blogService.deleteBlog(12);
//    }
//
//
//    @Test
//    public void findSeveralTypes() {
//        List<TypeTops> types = typeService.findSeveralTypes(5);
//        System.out.println(types);
//    }
//
//    @Test
//    public void findSeveralTopTags() {
//        List<TagTops> topTags = tagService.findSeveralTopTags(2);
//        System.out.println(topTags);
//    }
//
//    @Test
//    public void findTheLastBlog() {
//        List<Blog> theLastBlog = blogService.findTheLastBlog(2);
//        System.out.println(theLastBlog);
//    }
//
//
//    @Test
//    public void findTagsListByBlogId() {
//        List<Tag> tagsByBlogId = tagService.findTagsByBlogId(8);
//        System.out.println(tagsByBlogId);
//    }
//
//    @Test
//    public void findCommentsByBlogId() {
//        List<Comment> comments = commentService.findCommentsByBlogId(8);
//        for (Comment list : comments) {
//            System.out.println(list);
//        }
//    }
//
//    @Value(value = "${avatar}")
//    private String avatar;
//
//    @Test
//    public void test() {
//        System.out.println(avatar);
//    }
//
//    @Test
//    public void testCount() {
//        Integer count = typeService.findCount();
//        System.out.println(count);
//    }
//
//    @Test
//    void selectBlogByTypeId() {
//        List<Blog> blogByTypeId = blogService.findBlogByTypeId(12);
//        System.out.println(blogByTypeId);
//    }
//
//    @Test
//    public void findArchiveBlog() {
//        blogService.findArchiveBlog();
//    }
//
//    @Test
//    public void findAllBlog() {
//        List<Blog> allBlogByPage = blogService.findAllBlogByPage();
//        System.out.println(allBlogByPage.get(2));
//    }
//
//
//    @Test
//    public void selectSeveralTopTypes() {
//        List<TagTops> severalTopTags = tagService.findSeveralTopTags(6);
//        System.out.println(severalTopTags);
//    }
//
//    @Test
//    public void testTypeAdd() {
////        Type type = new Type();
////        type.setName("aaaaaaa");
////        typeService.saveType(type);
//
//    }
//
//    @Test
//    public void testDeleteTagById() {
//        tagService.deleteTagById(25);
//    }

}
