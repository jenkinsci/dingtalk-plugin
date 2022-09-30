import{_ as s,c as n,o as a,a as l}from"./app.77e7e65f.js";const p="/dingtalk-plugin/assets/link-example.cf43fc87.jpg",d=JSON.parse('{"title":"LINK \u7C7B\u578B\u7684\u6D88\u606F","description":"","frontmatter":{},"headers":[],"relativePath":"examples/link.md"}'),o={name:"examples/link.md"},e=l(`<h1 id="link-\u7C7B\u578B\u7684\u6D88\u606F" tabindex="-1">LINK \u7C7B\u578B\u7684\u6D88\u606F <a class="header-anchor" href="#link-\u7C7B\u578B\u7684\u6D88\u606F" aria-hidden="true">#</a></h1><div class="language-groovy"><button class="copy"></button><span class="lang">groovy</span><pre><code><span class="line"></span>
<span class="line"><span style="color:#A6ACCD;">pipeline {</span></span>
<span class="line"><span style="color:#A6ACCD;">    agent any</span></span>
<span class="line"><span style="color:#A6ACCD;">    stages {</span></span>
<span class="line"><span style="color:#A6ACCD;">        stage</span><span style="color:#89DDFF;">(</span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">link</span><span style="color:#89DDFF;">&#39;</span><span style="color:#89DDFF;">)</span><span style="color:#A6ACCD;">{</span></span>
<span class="line"><span style="color:#A6ACCD;">            steps {</span></span>
<span class="line"><span style="color:#A6ACCD;">                echo </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">\u6D4B\u8BD5 LINK \u6D88\u606F...</span><span style="color:#89DDFF;">&#39;</span></span>
<span class="line"><span style="color:#A6ACCD;">            }</span></span>
<span class="line"><span style="color:#A6ACCD;">            post {</span></span>
<span class="line"><span style="color:#A6ACCD;">                success {</span></span>
<span class="line"><span style="color:#A6ACCD;">                    dingtalk (</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#FFCB6B;">robot</span><span style="color:#89DDFF;">:</span><span style="color:#A6ACCD;"> </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">58f10219-2cd3-4de7-a1af-f85f4010c10a</span><span style="color:#89DDFF;">&#39;</span><span style="color:#A6ACCD;">,</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#FFCB6B;">type</span><span style="color:#89DDFF;">:</span><span style="color:#A6ACCD;"> </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">LINK</span><span style="color:#89DDFF;">&#39;</span><span style="color:#A6ACCD;">,</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#FFCB6B;">title</span><span style="color:#89DDFF;">:</span><span style="color:#A6ACCD;"> </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">\u4F60\u6709\u65B0\u7684\u6D88\u606F\uFF0C\u8BF7\u6CE8\u610F\u67E5\u6536</span><span style="color:#89DDFF;">&#39;</span><span style="color:#A6ACCD;">,</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#FFCB6B;">text</span><span style="color:#89DDFF;">:</span><span style="color:#A6ACCD;"> </span><span style="color:#89DDFF;">[</span></span>
<span class="line"><span style="color:#A6ACCD;">                            </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">\u6D4B\u8BD5\u94FE\u63A5\u7C7B\u578B\u7684\u6D88\u606F</span><span style="color:#89DDFF;">&#39;</span><span style="color:#89DDFF;">,</span></span>
<span class="line"><span style="color:#A6ACCD;">                            </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">\u5206\u884C\u663E\u793A\uFF0C\u54C8\u54C8\u54C8\u54C8</span><span style="color:#89DDFF;">&#39;</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#89DDFF;">]</span><span style="color:#A6ACCD;">,</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#FFCB6B;">messageUrl</span><span style="color:#89DDFF;">:</span><span style="color:#A6ACCD;"> </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">http://www.baidu.com</span><span style="color:#89DDFF;">&#39;</span><span style="color:#A6ACCD;">,</span></span>
<span class="line"><span style="color:#A6ACCD;">                        </span><span style="color:#FFCB6B;">picUrl</span><span style="color:#89DDFF;">:</span><span style="color:#A6ACCD;"> </span><span style="color:#89DDFF;">&#39;</span><span style="color:#C3E88D;">https://www.picdiet.com/img/photographer_compressed.jpg</span><span style="color:#89DDFF;">&#39;</span></span>
<span class="line"><span style="color:#A6ACCD;">                    )</span></span>
<span class="line"><span style="color:#A6ACCD;">                }</span></span>
<span class="line"><span style="color:#A6ACCD;">            }</span></span>
<span class="line"><span style="color:#A6ACCD;">        }</span></span>
<span class="line"><span style="color:#A6ACCD;">    }</span></span>
<span class="line"><span style="color:#A6ACCD;">}</span></span>
<span class="line"></span>
<span class="line"></span></code></pre></div><details class="details custom-block"><summary>\u67E5\u770B\u7ED3\u679C</summary><p><img src="`+p+'" alt="link-example"></p></details>',3),c=[e];function t(r,D,C,y,A,i){return a(),n("div",null,c)}const _=s(o,[["render",t]]);export{d as __pageData,_ as default};
