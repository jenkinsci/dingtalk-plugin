# Markdown

## 钉钉支持的语法

目前只支持 md 语法的子集，具体支持的元素如下：

```markdown
标题

# 一级标题

## 二级标题

### 三级标题

#### 四级标题

##### 五级标题

###### 六级标题

引用

> A man who stands for nothing will fall for anything.

文字加粗、斜体
**bold**
_italic_

链接
[this is a link](http://name.com)

图片
![](http://name.com/pic.jpg)

无序列表

- item1
- item2

有序列表

1. item1
2. item2
```

## 定制文字

经测试钉钉支持在 markdown 中使用 [font](https://developer.mozilla.org/zh-CN/docs/Web/HTML/Element/font) 标签。

示例：

1. <font color=red>红色文字</font>

```markdown
<font color=red>红色文字</font>
```

2. _<font color=red>红色-斜体文字</font>_

```markdown
_<font color=red>红色-斜体文字</font>_
```

3. **<font color=red>红色-加粗文字</font>**

```markdown
**<font color=red>红色-加粗</font>**
```

4. **_<font color=red>红色-斜体-加粗文字</font>_**

```markdown
**_<font color=red>红色-斜体-加粗</font>_**
```

5. <font color=red size=0 >红色-小号文字</font>

```markdown
<font color=red size=0 >红色-小号文字</font>
```

6. <font color=red size=3 >红色-正常大小文字</font>

```markdown
<font color=red size=3 >红色-正常大小文字</font>
```

7. <font color=red size=7 >红色-大号文字</font>

```markdown
<font color=red size=7 >红色-大号文字</font>
```

8. <font color=red  face=KaiTi>红色-楷体文字</font>

```markdown
<font color=red  face=KaiTi>红色-楷体文字</font>
```

::: warning

`face` 属性暂未经过测试，在钉钉中表现未知。

:::

::: danger

同样的 markdown 内容在 `MARKDOWN` 与 `ACTION_CARD` 消息中表现可能不一致，系正常现象，在意的可以去钉钉官方群反馈。
:::

![dingTalkLogo](/assets/dingtalk-logo.png)

“钉钉群号： 11733391
