$(document).ready(function() {
	const csrfToken = $('meta[name="_csrf"]').attr('content');
	const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

	// Make the service rectangles draggable
	$(".service-rectangle").draggable({
		revert: "invalid", // Rectangle returns to original position if not dropped in a valid slot
		containment: "body", // Restrict dragging within the screen
		helper: "clone", // Display a clone while dragging
		cursor: "move" // Change cursor to move icon
	});

	// Make the empty slots droppable
	$(".empty-slot").droppable({
		accept: ".service-rectangle", // Only accept rectangles
		drop: function(event, ui) {
			let draggedText = ui.draggable.text();
			let draggedId = ui.draggable.attr('id');
			let previousRef = $(this).attr('data-ref');
			if (previousRef != "") {
				$("#" + previousRef).css('background-color', '#0073e6');
				$("#" + previousRef).draggable("enable");
			}
			ui.draggable.draggable("disable").css("background-color", "gray"); // Disable the rectangle after it's dropped
			$(this).attr('data-ref', draggedId);
			$(this).text(draggedText); // Set the text of the slot to the dragged rectangle's text
		}
	});

	// Prevent form submission if values are not filled
	$("#check-button").click(function(event) {
		event.preventDefault(); // Prevent form submission
		const guess1 = $("#guess1").text();
		const guess2 = $("#guess2").text();
		const guess3 = $("#guess3").text();

		if (!guess1 || !guess2 || !guess3) {
			alert("Please drag and drop all services into the slots.");
		}
		else{
			var formData = new FormData(); // Currently empty
			formData.append('guess1', $('#guess1').text());
			formData.append('guess2', $('#guess2').text());
			formData.append('guess3', $('#guess3').text());

            // Perform AJAX POST request
            $.ajax({
                url: '/check', // Replace with your endpoint
                type: 'POST',
                contentType: 'application/json',
                data: formData,
                processData: false, // Prevent jQuery from processing the data
            	contentType: false, 
                beforeSend: function (xhr) {
                    // Set the CSRF token in the header
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function (response) {
					// Display the results
	                let resultText = `Attempts: ${response.attempts} <br>`;
					if(response.inCorrectPlace == 3){
						resultText += `<br>Time Taken: ${response.elapsedTime}s <br>Congratulations, you won!`;
						$("#winSound")[0].play();
	                    $("#check-button").prop("disabled", true); // Disable the check button
					}
					else{
						resultText += `Correctly Placed: ${response.inCorrectPlace} <br>
							           Wrongly Placed: ${response.inWrongPlace}`;
					   $(".empty-slot").text("").attr("data-ref", "");
			           // Reset draggable rectangles
			           $(".service-rectangle").draggable("enable").css("background-color", "#0073e6");
					}
	                $("#result").html(resultText);
					$("html, body").animate({ scrollTop: $(document).height() }, 1000);
                },
                error: function (xhr, status, error) {
                    console.error('Error:', error);
                    alert('Request failed: ' + xhr.status + ' - ' + xhr.responseText);
                }
            });
		}
	});

	// Reset the game state
	function resetGame() {
		location.reload();
	}
});