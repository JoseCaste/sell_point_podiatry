var defaultEvents = [
    {
      // Custom repeating event
      id: 999,
      title: 'Repeating Event',
      start: '2022-03-09T09:00',
    }
];
$('#calendar').fullCalendar({
    header: {
    left: 'prev,next today',
    center: 'title',
    right: 'month,agendaWeek,agendaDay'
  },
    editable: true,
    eventSources: [jsonDate]

});