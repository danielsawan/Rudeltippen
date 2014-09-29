$(document).ready(function(){
    $('.table-responsive').responsiveTable({
    	addFocusBtn: false,
    	addDisplayAllBtn: false,
    	stickyTableHeader: false
    });
	$(".dropdown-btn-group").hide();
    $(".pagination").rPage();
	$('.credits-modal').click(function() {
		$('#credits').modal();
	}); 
	$('.alert-warning').delay(20000).slideToggle();
	$('.alert-success').delay(5000).slideToggle();
	$('.alert-danger').delay(10000).slideToggle();
	$('.btn-success').click(function() {
		var btn = $(this);
		btn.button('loading');
	});   	
	$('img').on().tooltip();
	$('.admintooltip').on().tooltip();
	$('.bonustips').on().tooltip();
	$('.jobtooltip').on().tooltip();
	window.addEventListener("load", function() {
		setTimeout(function() {
			window.scrollTo(0, 1);
		}, 0);
	});
    $('#calculations').click(function() {
    	var opts = {
    			lines: 13, // The number of lines to draw
    			length: 11, // The length of each line
    			width: 5, // The line thickness
    			radius: 17, // The radius of the inner circle
    			corners: 1, // Corner roundness (0..1)
    			rotate: 0, // The rotation offset
    			color: '#FFF', // #rgb or #rrggbb
    			speed: 1, // Rounds per second
    			trail: 60, // Afterglow percentage
    			shadow: false, // Whether to render a shadow
    			hwaccel: false, // Whether to use hardware acceleration
    			className: 'spinner', // The CSS class to assign to the spinner
    			zIndex: 2e9, // The z-index (defaults to 2000000000)
    			top: 'auto', // Top position relative to parent in px
    			left: 'auto' // Left position relative to parent in px
    		};
    		var target = document.createElement("div");
    		document.body.appendChild(target);
    		var spinner = new Spinner(opts).spin(target);
    		iosOverlay({
    			text: "Loading",
    			duration: 10000,
    			spinner: spinner
    		});
    }); 
    if ($('.editable').length > 0){
    	$('.editable').editable(
        		{
        			success: function(response, newValue) {
       					iosOverlay({
		        			text: "Saved!",
		        			duration: 2e3,
		        			icon: "/assets/img/check.png"
		        		});
        			}	
        		}
        	);
        	$(".updatable").click(function() {
        		var url = $(this).attr("data-url");
        		if (url != null) {
        			$.get(url)
        			.done(function() {
        				iosOverlay({
			        		text: "Saved!",
			        		duration: 2e3,
			        		icon: "/assets/img/check.png"
			        	});
        			})
        			.fail(function() {
        				iosOverlay({
			        		text: "Error!",
			        		duration: 2e3,
			        		icon: "/assets/img/cross.png"
			        	});
        			});
        		}
        });	        
    }
    $('#extratips').on('hidden.bs.collapse', function () {
    	$('#collapseExtra').html("<span class=\"glyphicon glyphicon-circle-arrow-down\">");
    });
    $('#extratips').on('show.bs.collapse', function () {
    	$('#collapseExtra').html("<span class=\"glyphicon glyphicon-circle-arrow-up\">");
    })
});

$(document).on("click", ".loading-avatar", function(e) {
	var opts = {
		lines: 13, // The number of lines to draw
		length: 11, // The length of each line
		width: 5, // The line thickness
		radius: 17, // The radius of the inner circle
		corners: 1, // Corner roundness (0..1)
		rotate: 0, // The rotation offset
		color: '#FFF', // #rgb or #rrggbb
		speed: 1, // Rounds per second
		trail: 60, // Afterglow percentage
		shadow: false, // Whether to render a shadow
		hwaccel: false, // Whether to use hardware acceleration
		className: 'spinner', // The CSS class to assign to the spinner
		zIndex: 2e9, // The z-index (defaults to 2000000000)
		top: 'auto', // Top position relative to parent in px
		left: 'auto' // Left position relative to parent in px
	};
	var target = document.createElement("div");
	document.body.appendChild(target);
	var spinner = new Spinner(opts).spin(target);
	iosOverlay({
		text: "Loading",
		duration: 10000,
		spinner: spinner
	});
});