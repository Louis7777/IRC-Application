<!DOCTYPE html>
<html lang="en">
    <head>
        <title><?php echo $page_title; ?></title>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="keywords" content="Java,Web,Chat" />
        <meta name="description" content="An IRC Web Client to connect to your favorite servers." />

        <link rel="shortcut icon" href="./favicon.ico" type="image/x-icon" />

        <link rel="stylesheet" href="./includes/css/layout.css" type="text/css" />
        <?php
        /* INCLUDE specific style sheets. */
        if ($current_file == "index.php") {
            echo "<link rel=\"stylesheet\" href=\"./includes/css/index.css\" type=\"text/css\" />";
        } else if ($current_file == "faq.php") {
            echo "<link rel=\"stylesheet\" href=\"./includes/css/faq.css\" type=\"text/css\" />";
        } else if ($current_file == "help.php") {
            echo "<link rel=\"stylesheet\" href=\"./includes/css/help.css\" type=\"text/css\" />";
        } else if ($current_file == "download.php") {
            echo "<link rel=\"stylesheet\" href=\"./includes/css/download.css\" type=\"text/css\" />";
        }
        ?>

        <script src="./includes/js/brain.js" type="text/javascript"></script>

        <!--[if lt IE 9]>
        <script type="text/javascript">
        document.createElement("nav");
        document.createElement("header");
        document.createElement("footer");
        document.createElement("section");
        document.createElement("aside");
        document.createElement("article");
        </script>
        <![endif]-->

    </head>
    <body>
        <!-- OPEN WRAPPER -->
        <div id="wrapper">

            <!-- OPEN HEADER -->
            <header id="header">
                <img src="./images/fry.png" width="256" height="256" alt="Logo" style="float:left;" />
                <div style="clear: both;"></div>
                <div style="position: absolute; top: 18px; left: 250px; height: 40px; font-weight:500; font-size:40px; color:#3aaa2b;">Java Web Chat</div>
                <nav id="navigation">
                    <ul>
                        <li><a href="./index.php">Chat</a></li>
                        <li><a href="./faq.php" title="internal links">FAQ</a></li>
                        <li><a href="./help.php">Commands</a></li>
                        <li><a href="./download.php">Download</a></li>
                    </ul>
                </nav>
            </header>
            <!-- CLOSE HEADER -->

