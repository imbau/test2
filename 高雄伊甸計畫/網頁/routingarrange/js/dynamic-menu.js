jQuery.noConflict();  
jQuery(document).ready(function($){
	$(".dynamic-menu").hide();
	$(".close").click(
	function(){
		$(".dynamic-menu").slideToggle();
	}
	);
});

