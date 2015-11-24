$(document).ready(function() {

    console.log('ready!');

    var timeoutID = null;

    //mark favs:
    if($.cookie('favlist')) {
        $(".ama_elem").each(function() {
            var id = $(this).attr("id");
            if($.cookie('favlist').indexOf(id) >= 0) { //  is fav
                $(this).children(".fav").addClass("liked");
            }
        });
    }

    function search(str) {
        console.log("searching: " + str);

        if (str) {
            $(".ama_elem a").each(function(){
                if ($(this).text() == "" || $(this).text().toLowerCase().indexOf(str.toLowerCase()) < 0){
                    $(this).parents(".ama_elem").css("display", "none");
                } else {
                    $(this).parents(".ama_elem").css("display", "block");
                    //console.log("Found!")
                }
            });
        } else {
            $(".ama_elem").css("display", "block");
        }
    }

    $('#searchbar').on('change keyup copy paste cut', function() {
        clearTimeout(timeoutID);
        var $target = $(this);
        timeoutID = setTimeout(function() { search($target.val()); }, 500);
    });

    $(".favButton").click(function(){
        if($(this).hasClass("active")) {
            $(this).removeClass("active");
            $(this).text("Nur Favoriten zeigen");
            $(".ama_elem").css("display", "block");
        } else {
            $(this).addClass("active");
            $(this).text("Alle zeigen")
            $(".ama_elem").each(function(){
                if($(this).children(".liked").length == 0){
                    $(this).css("display", "none");
                }
            })
        }

    });

    $(".fav").click(function() {
        console.log("adding item");

        if($(this).hasClass("liked")) { //remove from cookie
            var split = $.cookie("favlist").split(";");
            var result = "";

            var id = $(this).parents(".ama_elem").attr("id");

            for (i = 0; i < split.length; i++) {
                if(split[i] != id)
                    result = result + ";" + split[i];
            }

            $.cookie("favlist", result);
            $(this).removeClass("liked");
        } else {
            $(this).addClass("liked");

            if(!$.cookie('favlist'))
                $.cookie('favlist', '');

            $.cookie('favlist', $.cookie('favlist') + $(this).parents(".ama_elem").attr("id") + ";");

            console.log("cookie content: " + $.cookie('favlist'));
        }

    })

});

