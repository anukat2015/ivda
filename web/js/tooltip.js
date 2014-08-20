function initializeTooltip() {
    $(".timeline-event-dot-container").qtip({
        show: 'click',
        hide: 'unfocus',
        content: {
            text: function (event, api) {
                var entityID = getSelectedValue(5);
                if(entityID == undefined) {
                    return;
                }
                $.ajax({
                    url: getEventEntityURL(entityID)
                }).then(function (content) {
                    // Set the tooltip content upon successful retrieval
                    api.set('content.text', content);
                }, function (xhr, status, error) {
                    // Upon failure... set the tooltip content to error
                    api.set('content.text', status + ': ' + error);
                });
                return 'Loading...'; // Set some initial text
            }
        }
    });
}