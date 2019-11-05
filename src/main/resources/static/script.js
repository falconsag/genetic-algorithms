var WALL = -1;
var canvasSize = 400;
var slider = $("#slider");
var fieldSize;
var sliderVal = slider.val();
var canvas = $("#myCanvas");
var strokeWidthPixel = 2;
canvas.attr("width", canvasSize)
canvas.attr("height", canvasSize)
var ctx = canvas[0].getContext("2d");
var wallColor = "#000000";
var robotColor = "#FF0000";
var foodColor = "#ff66cc";
var sightDefaultColor = "#48CDEF";
var sightSeesColor = "#48EF69";
var seesAlpha = 0.62
var objectsAhead = {0: "empty", 1: "wall"};
var simulation;

$("#generateButton").on("click", function () {
    var genLimit = $("input[name='genLimit']").val();
    var moveCost = $("input[name='moveCost']").val();
    var turnCost = $("input[name='turnCost']").val();
    var doNothing = $("input[name='doNothingCost']").val();
    var numberOfSimulateSteps = $("input[name='numberOfSimulateSteps']").val();
    var foodMin = $("input[name='foodMin']").val();
    var foodMax = $("input[name='foodMax']").val();
    var foodDecrease = $("input[name='foodDecrease']").val();
    var mazeSize = $("input[name='mazeSize']").val();


    var url = "http://localhost:8080/evolve?genLimit=" + genLimit
        + "&moveCost=" + moveCost
        + "&turnCost=" + turnCost
        + "&doNothingCost=" + doNothing
        + "&numberOfSimulateSteps=" + numberOfSimulateSteps
        + "&foodMin=" + foodMin
        + "&foodMax=" + foodMax
        + "&foodDecrease=" + foodDecrease
        + "&mazeSize=" + mazeSize

    console.log("evolving chromosomes..")
    $.getJSON(url, function (simulationResponse) {
        simulation = simulationResponse
        sliderVal = 0
        slider.val(sliderVal)
        console.log("done..")
        fieldSize = Math.sqrt(simulation.fields.length)
        slider.attr("max", simulation.states.length - 1)
        drawState(sliderVal)
        slider.on("input change", function () {
            sliderVal = slider.val();
            drawState(sliderVal);
        })
    });
})


function drawState(stateIndex) {
    ctx.clearRect(0, 0, canvas[0].width, canvas[0].height);

    var fields = simulation.fields
    var cellSize = canvasSize / fieldSize;
    var sightDistance = simulation.sightDistance;

    drawGrid(wallColor);
    drawField();
    drawFood(stateIndex);
    drawRobot(robotColor, stateIndex, sightDistance);


    function drawFood(stateIndex) {
        var food = simulation.states[stateIndex].foods[0];
        var foodPct = food.value / simulation.foodMaxValue;
        var coord = food.coord;
        var foodH = cellSize * foodPct;
        var prevFillStyle = ctx.fillStyle;
        ctx.fillStyle = foodColor;
        ctx.fillRect(coord.x * cellSize, coord.y * cellSize + (cellSize - foodH), cellSize, foodH);
        ctx.fillStyle = prevFillStyle;
    }

    function drawField() {
        for (var i = 0; i < fields.length; i++) {
            var block = fields[i]
            if (block == WALL) {
                fillRect(toXY(i, fieldSize), wallColor, 1.0)
            }
        }
    }

    function fillRect(coord, color, alpha) {
        var prevFillStyle = ctx.fillStyle;
        var prevAlpha = ctx.globalAlpha;
        ctx.fillStyle = color;
        ctx.globalAlpha = alpha;
        ctx.fillRect(coord.x * cellSize, coord.y * cellSize, cellSize, cellSize);
        ctx.fillStyle = prevFillStyle;
        ctx.globalAlpha = prevAlpha;
    }

    function toXY(i, size) {
        var x = i % size;
        var y = Math.floor(i / size);
        return {x: x, y: y};
    }

    function drawGrid(color) {
        var prevColor = ctx.fillStyle;
        ctx.fillStyle = color;
        for (var i = 0; i < fieldSize + 1; i++) {
            ctx.beginPath();
            ctx.moveTo(i * cellSize, 0);
            ctx.lineTo(i * cellSize, canvasSize + strokeWidthPixel);
            ctx.stroke();

            ctx.beginPath();
            ctx.moveTo(0, i * cellSize);
            ctx.lineTo(canvasSize + strokeWidthPixel, i * cellSize);
            ctx.stroke();
        }
        ctx.fillStyle = prevColor;
    }

    function fillRectColorAndAlpha(x, y, w, h, color, alpha) {
        var prevAlpha = ctx.globalAlpha;
        var prevcolor = ctx.fillStyle;
        ctx.globalAlpha = alpha;
        ctx.fillStyle = color;
        ctx.fillRect(x, y, w, h)
        ctx.globalAlpha = prevAlpha;
        ctx.fillStyle = prevcolor;
    }

    function drawRobot(color, stateIndex, sightDistance) {
        var prevColor = ctx.fillStyle;
        ctx.fillStyle = color;
        var x = simulation.states[stateIndex].robot.coord.x * cellSize;
        var y = simulation.states[stateIndex].robot.coord.y * cellSize;
        var dirX = simulation.states[stateIndex].robot.dir.x
        var dirY = simulation.states[stateIndex].robot.dir.y
        var seesFood = simulation.states[stateIndex].seesFood;
        var ahead = simulation.states[stateIndex].ahead;
        var rHP = simulation.states[stateIndex].rHP;
        var fHP = simulation.states[stateIndex].fHP;
        var c = seesFood ? sightSeesColor : sightDefaultColor;
        var sensorData = {
            "ahead": objectsAhead[ahead],
            "seesFood": seesFood,
            "rHP": rHP,
            "fHP": fHP
        };
        console.log(sensorData)
        if (dirX == 1) {
            fillRectColorAndAlpha(x, y - (sightDistance * cellSize), (1 + sightDistance) * cellSize, (1 + 2 * sightDistance) * cellSize, c, seesAlpha);
            ctx.beginPath();
            ctx.moveTo(x, y);
            ctx.lineTo(x + cellSize, y + cellSize / 2);
            ctx.lineTo(x, y + cellSize);
            ctx.fill();

        } else if (dirX == -1) {
            var alpha = seesFood ? 0.8 : seesAlpha;
            fillRectColorAndAlpha(x + cellSize, y - (sightDistance * cellSize), -(1 + sightDistance) * cellSize, (1 + 2 * sightDistance) * cellSize, c, seesAlpha);
            ctx.beginPath();
            ctx.moveTo(x + cellSize, y);
            ctx.lineTo(x, y + cellSize / 2);
            ctx.lineTo(x + cellSize, y + cellSize);
            ctx.fill();
        }
        if (dirY == 1) {
            var alpha = seesFood ? 0.8 : seesAlpha;
            fillRectColorAndAlpha(x - (sightDistance * cellSize), y, (1 + 2 * sightDistance) * cellSize, (1 + sightDistance) * cellSize, c, seesAlpha);
            ctx.beginPath();
            ctx.moveTo(x, y);
            ctx.lineTo(x + cellSize, y);
            ctx.lineTo(x + cellSize / 2, y + cellSize);
            ctx.fill();
        } else if (dirY == -1) {
            var alpha = seesFood ? 0.8 : seesAlpha;
            fillRectColorAndAlpha(x - (sightDistance * cellSize), y + cellSize, (1 + 2 * sightDistance) * cellSize, -(1 + sightDistance) * cellSize, c, seesAlpha);
            ctx.beginPath();
            ctx.moveTo(x, y + cellSize);
            ctx.lineTo(x + cellSize, y + cellSize);
            ctx.lineTo(x + cellSize / 2, y);
            ctx.fill();
        }
        ctx.fillStyle = prevColor;
    }


}
