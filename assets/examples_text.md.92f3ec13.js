import{_ as s,c as a,o as n,a as p}from"./app.77e7e65f.js";const l="/dingtalk-plugin/assets/text-example.372e4167.jpg",_=JSON.parse('{"title":"TEXT \u7C7B\u578B\u7684\u6D88\u606F","description":"","frontmatter":{},"headers":[],"relativePath":"examples/text.md"}'),o={name:"examples/text.md"},e=p(`<h1 id="text-\u7C7B\u578B\u7684\u6D88\u606F" tabindex="-1">TEXT \u7C7B\u578B\u7684\u6D88\u606F <a class="header-anchor" href="#text-\u7C7B\u578B\u7684\u6D88\u606F" aria-hidden="true">#</a></h1><div class="language-groovy"><button class="copy"></button><span class="lang">groovy</span><pre><code><span class="line"></span>
<span class="line"><span style="color:#A6ACCD;">pipeline {</span></span>
<span class="line"><span style="color:#A6ACCD;">    agent any</span></span>
<span class="line"><span style="color:#A6ACCD;">    stages {</span></span>
<span class="line"><span style="color:#A6ACCD;">        stage</span><span style="color:#89DDFF;">(</span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">text</span><span style="color:#89DDFF;">&#39;</span><span style="color:#89DDFF;">)</span><span style="color:#A6ACCD;">{</span></span>
<span class="line"><span style="color:#A6ACCD;">            steps {</span></span>
<span class="line"><span style="color:#A6ACCD;">                echo </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">\u6D4B\u8BD5 TEXT \u6D88\u606F...</span><span style="color:#89DDFF;">&#39;</span></span>
<span class="line"><span style="color:#A6ACCD;">            }</span></span>
<span class="line"><span style="color:#A6ACCD;">            post {</span></span>
<span class="line"><span style="color:#A6ACCD;">                success {</span></span>
<span class="line"><span style="color:#A6ACCD;">                    dingtalk (</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#FFCB6B;">robot</span><span style="color:#89DDFF;">:</span><span style="color:#A6ACCD;"> </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">58f10219-2cd3-4de7-a1af-f85f4010c10a</span><span style="color:#89DDFF;">&#39;</span><span style="color:#A6ACCD;">,</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#FFCB6B;">type</span><span style="color:#89DDFF;">:</span><span style="color:#A6ACCD;"> </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">TEXT</span><span style="color:#89DDFF;">&#39;</span><span style="color:#A6ACCD;">,</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#FFCB6B;">text</span><span style="color:#89DDFF;">:</span><span style="color:#A6ACCD;"> </span><span style="color:#89DDFF;">[</span></span>
<span class="line"><span style="color:#A6ACCD;">                            </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">\u6D4B\u8BD5\u6587\u672C\u7C7B\u578B\u7684\u6D88\u606F</span><span style="color:#89DDFF;">&#39;</span><span style="color:#89DDFF;">,</span></span>
<span class="line"><span style="color:#A6ACCD;">                            </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">\u5206\u884C\u663E\u793A\uFF0C\u54C8\u54C8\u54C8\u54C8</span><span style="color:#89DDFF;">&#39;</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#89DDFF;">]</span><span style="color:#A6ACCD;">,</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#FFCB6B;">at</span><span style="color:#89DDFF;">:</span><span style="color:#A6ACCD;"> </span><span style="color:#89DDFF;">[</span></span>
<span class="line"><span style="color:#A6ACCD;">                            </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">18516601234</span><span style="color:#89DDFF;">&#39;</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#89DDFF;">]</span></span>
<span class="line"><span style="color:#A6ACCD;">                    )</span></span>
<span class="line"><span style="color:#A6ACCD;">                }</span></span>
<span class="line"><span style="color:#A6ACCD;">            }</span></span>
<span class="line"><span style="color:#A6ACCD;">        }</span></span>
<span class="line"><span style="color:#A6ACCD;">    }</span></span>
<span class="line"><span style="color:#A6ACCD;">}</span></span>
<span class="line"></span>
<span class="line"></span></code></pre></div><details class="details custom-block"><summary>\u67E5\u770B\u7ED3\u679C</summary><p><img src="`+l+'" alt="text-example"></p></details>',3),t=[e];function c(r,D,C,y,A,F){return n(),a("div",null,t)}const d=s(o,[["render",c]]);export{_ as __pageData,d as default};
