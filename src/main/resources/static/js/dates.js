$('#calendar').fullCalendar({
    header: {
    left: 'prev,next today',
    center: 'title',
    right: 'month,agendaWeek,agendaDay'
  },
  viewRender: function(currentView){
  var minDate = moment(),
  		maxDate = moment().add(2,'weeks');
  		// Past
  		if (minDate >= currentView.start && minDate <= currentView.end) {
  			$(".fc-prev-button").prop('disabled', true);
  			$(".fc-prev-button").addClass('fc-state-disabled');
  		}
  		else {
  			$(".fc-prev-button").removeClass('fc-state-disabled');
  			$(".fc-prev-button").prop('disabled', false);
  		}
  },
    editable: true,
    eventSources: [jsonDate]
});