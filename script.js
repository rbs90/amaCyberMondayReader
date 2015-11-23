$(document).ready(function() {

    console.log('ready!');

    var timeoutID = null;

    function search(str) {
        console.log("searching: " + str);

        if (str) {
            $(".ama_elem a").each(function(){
                if ($(this).text() == "" || $(this).text().toLowerCase().indexOf(str.toLowerCase()) <= 0){
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

});