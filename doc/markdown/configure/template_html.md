# Template html

You can configure template for html.

### _template.html (example)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="${assets}/main.css"/>
    <title>${h1} - md2html</title>
    ${gtag}
</head>
<body>
<header>
    <nav class="header-nav">
        <a href="${relative}index.html">
            <img id="shirates-logo" src="${assets}/md2html-logo-banner.png">
        </a>
    </nav>
</header>
<div id="readme" class="Box">
    <article class="markdown-body container-lg">
        ${contentHtml}
    </article>
</div>
</body>
</html>
```

## Template variables

| variable       | description            |
|----------------|------------------------|
| ${assets}      | Path of `_assets`      |
| ${relative}    | Relative path to root  |
| ${h1}          | Value of first h1      |
| ${gtag}        | Place holder for gtag  |
| ${contentHtml} | Converted html from md |

### Link

- [index](../index.md)
