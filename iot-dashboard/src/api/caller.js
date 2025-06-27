async function RGBSelector(r, g, b) {
    const val = {"A2": r, "A3": g, "A5": b};
    for (const [key, value] of Object.entries(val)) {
        await fetch('/v1/room/led', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                pin: key,
                value: value,
            })
        })
            .then(res => res.json())
            .then(data => {
                console.log('Antwort vom Server:', data);
            })
            .catch(err => {
                console.error('Fehler beim API-Call:', err);
            });
    }
}

async function OnOffSwitch(status) {
    await fetch('/v1/room/lamp', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            turn: status
        })
    })
        .then(res => res.json())
        .then(data => {
            console.log('Antwort vom Server:', data);
        })
        .catch(err => {
            console.error('Fehler beim API-Call:', err);
        });
}