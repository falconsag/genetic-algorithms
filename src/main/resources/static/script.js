var WALL = -1;
var CLEAR = 0;
var PLAYER = -100

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
var fitnessChartCtx = document.getElementById('fitnessChart').getContext('2d');
var fitnessChart
var animationMs = getAnimationMS()
var isAnimation = false;

var genLimit = 0;
var evolving = false;

var editor = {
    editorMode: isEditorChecked(),
    painting: false,
    mazeSize: getMazeSize(),
    mapArr: initializeMap(getMazeSize()),
    brush: CLEAR,
    cellSize: canvasSize / getMazeSize(),
    robot: {
        x: 1,
        y: 1,
        dirX: 1,
        dirY: 0
    }
}
reDrawEditor()

/**
 * canvas: html DOM canvas element, not the jQuery one
 * @param canvas
 * @param event
 */
function getMousePosition(canvas, event) {
    var rect = canvas.getBoundingClientRect();
    var x = event.clientX - rect.left;
    var y = event.clientY - rect.top;
    return {x: x, y: y}
}


$("#generateButton").on("click", function () {
    evolving = true;

    var getSimulationsFnct = function getSimulations() {
        $.ajax({
            contentType: 'application/json',
            dataType: 'json',
            success: function (resp) {
                console.log(resp.length)
                if (resp.length < genLimit) {
                    setTimeout(getSimulationsFnct, 50);
                }
            },
            type: 'GET',
            url: "http://localhost:8080/getSimulations"
        })
    }
    setTimeout(getSimulationsFnct, 50);


    disableEditorMode();
    genLimit = $("input[name='genLimit']").val();
    var moveCost = $("input[name='moveCost']").val();
    var turnCost = $("input[name='turnCost']").val();
    var doNothing = $("input[name='doNothingCost']").val();
    var numberOfSimulateSteps = $("input[name='numberOfSimulateSteps']").val();
    var foodMin = $("input[name='foodMin']").val();
    var foodMax = $("input[name='foodMax']").val();
    var foodDecrease = $("input[name='foodDecrease']").val();
    var mazeSize = getMazeSize();
    var numberOfSimulationsToAvg = $("input[name='numberOfSimulationsToAvg']").val();


    var req = {
        editorConfig: editor,
        simulatorConfig: {
            genLimit: genLimit,
            moveCost: moveCost,
            turnCost: turnCost,
            doNothingCost: doNothing,
            numberOfSimulateSteps: numberOfSimulateSteps,
            foodMin: foodMin,
            foodMax: foodMax,
            foodDecrease: foodDecrease,
            numberOfSimulationsToAvg: numberOfSimulationsToAvg
        }
    }

    var url = "http://localhost:8080/evolve"

    $.ajax({
        contentType: 'application/json',
        data: JSON.stringify(req),
        dataType: 'json',
        success: function (simulationResponse) {
            evolving = false;
            simulation = simulationResponse
            sliderVal = 0
            slider.val(sliderVal)
            console.log("done..")

            slider.attr("max", simulation.states.length - 1)
            drawState(sliderVal)
            slider.on("input change", function () {
                sliderVal = slider.val();
                drawState(sliderVal);
            })

            var chartData = simulationResponse.fitnessValues
            var labels = []
            for (var i = 0; i < chartData.length; i++) {
                labels.push("Generation: " + i)
            }
            if (typeof fitnessChart !== 'undefined') {
                fitnessChart.data.labels = [];
                fitnessChart.data.datasets[0].data = [];
                fitnessChart.data.labels = [].concat(labels);
                fitnessChart.data.datasets[0].data = [].concat(chartData);
                fitnessChart.update();
            } else {
                fitnessChart = new Chart(fitnessChartCtx, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Fitness over generations',
                            backgroundColor: 'rgb(255, 99, 132)',
                            borderColor: 'rgb(255, 99, 132)',
                            data: chartData
                        }]
                    },
                    // Configuration options go here
                    options: {}
                });
            }
        },
        type: 'POST',
        url: url
    })

})


function drawRobotBody(color, dirX, x, y, sightDistance, cellSize, c, seesFood, dirY) {
    var prevColor = ctx.fillStyle;
    ctx.fillStyle = color;
    if (dirX == 1) {
        if (typeof sightDistance !== "undefined") {
            fillRectColorAndAlpha(x, y - (sightDistance * cellSize), (1 + sightDistance) * cellSize, (1 + 2 * sightDistance) * cellSize, c, seesAlpha);
        }
        ctx.beginPath();
        ctx.moveTo(x, y);
        ctx.lineTo(x + cellSize, y + cellSize / 2);
        ctx.lineTo(x, y + cellSize);
        ctx.fill();

    } else if (dirX == -1) {
        var alpha = seesFood ? 0.8 : seesAlpha;
        if (typeof sightDistance !== "undefined") {
            fillRectColorAndAlpha(x + cellSize, y - (sightDistance * cellSize), -(1 + sightDistance) * cellSize, (1 + 2 * sightDistance) * cellSize, c, seesAlpha);
        }
        ctx.beginPath();
        ctx.moveTo(x + cellSize, y);
        ctx.lineTo(x, y + cellSize / 2);
        ctx.lineTo(x + cellSize, y + cellSize);
        ctx.fill();
    }
    if (dirY == 1) {
        var alpha = seesFood ? 0.8 : seesAlpha;
        if (typeof sightDistance !== "undefined") {
            fillRectColorAndAlpha(x - (sightDistance * cellSize), y, (1 + 2 * sightDistance) * cellSize, (1 + sightDistance) * cellSize, c, seesAlpha);
        }
        ctx.beginPath();
        ctx.moveTo(x, y);
        ctx.lineTo(x + cellSize, y);
        ctx.lineTo(x + cellSize / 2, y + cellSize);
        ctx.fill();
    } else if (dirY == -1) {
        var alpha = seesFood ? 0.8 : seesAlpha;
        if (typeof sightDistance !== "undefined") {
            fillRectColorAndAlpha(x - (sightDistance * cellSize), y + cellSize, (1 + 2 * sightDistance) * cellSize, -(1 + sightDistance) * cellSize, c, seesAlpha);
        }
        ctx.beginPath();
        ctx.moveTo(x, y + cellSize);
        ctx.lineTo(x + cellSize, y + cellSize);
        ctx.lineTo(x + cellSize / 2, y);
        ctx.fill();
    }
    ctx.fillStyle = prevColor;
}

function drawState(stateIndex) {
    if (editor.editorMode) {
        return
    }
    clearMaze();

    var simulationFieldsArr = simulation.fields
    var mazeSize = Math.sqrt(simulationFieldsArr.length)
    var cellSize = canvasSize / mazeSize;
    var sightDistance = simulation.sightDistance;

    drawField(simulationFieldsArr, cellSize, mazeSize);
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


    function drawRobot(color, stateIndex, sightDistance) {
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

        drawRobotBody(color, dirX, x, y, sightDistance, cellSize, c, seesFood, dirY);
    }
}

function drawField(mapArr, cellSize, mazeSize) {
    drawGrid(cellSize, mazeSize);
    for (var i = 0; i < mapArr.length; i++) {
        var block = mapArr[i]
        if (block == WALL) {
            fillRect(toXY(i, mazeSize), wallColor, 1.0, cellSize)
        }
    }
}

function drawGrid(cellSize, mazeSize) {
    var prevColor = ctx.fillStyle;
    ctx.fillStyle = "#000000";
    for (var i = 0; i < mazeSize + 1; i++) {
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

function getAnimationMS() {
    return parseInt($("input[name='replayDelay']").val());
}

function isSimulationPresent() {
    return typeof simulation !== 'undefined';
}

function getMazeSize() {
    return $("input[name='mazeSize']").val();
}

function initializeMap(mazeSize) {
    var mazeLength = mazeSize * mazeSize;
    var arr = new Array(mazeLength).fill(0);
    for (var i = 0; i < mazeSize; i++) {

        arr[xyToI(i, 0, mazeSize)] = WALL;
        arr[xyToI(0, i, mazeSize)] = WALL;
        arr[xyToI(i, mazeSize - 1, mazeSize)] = WALL;
        arr[xyToI(mazeSize - 1, i, mazeSize)] = WALL;
    }
    return arr;
}

function xyToI(x, y, size) {
    return y * size + x;
}

//DRAW FUNCTIONS---------------------------------------
function fillRect(coord, color, alpha, size) {
    var prevFillStyle = ctx.fillStyle;
    var prevAlpha = ctx.globalAlpha;
    ctx.fillStyle = color;
    ctx.globalAlpha = alpha;
    ctx.fillRect(coord.x * size, coord.y * size, size, size);
    ctx.fillStyle = prevFillStyle;
    ctx.globalAlpha = prevAlpha;
}

function toXY(i, size) {
    var x = i % size;
    var y = Math.floor(i / size);
    return {x: x, y: y};
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

function reDrawEditor() {
    if (!editor.editorMode) {
        return
    }
    clearMaze()
    drawField(editor.mapArr, canvasSize / editor.mazeSize, editor.mazeSize)
    if (editor.painting) {
        fillRectColorAndAlpha(0, 0, canvas[0].width, canvas[0].height, "#bbe6e4", 0.3)
    }
    var cellSize = editor.cellSize
    drawRobotBody(robotColor, editor.robot.dirX,
        editor.robot.x * cellSize, editor.robot.y * cellSize,
        undefined, editor.cellSize, undefined, false, editor.robot.dirY)
}

function clearMaze() {
    ctx.clearRect(0, 0, canvas[0].width, canvas[0].height);
}

//END DRAW FUNCTIONS---------------------------------------

//event handlers
$("input[name='editorModeCheckBox']").change(function () {
    editor.editorMode = $(this).is(':checked')
    if (editor.editorMode) {
        reDrawEditor()
    } else {
        if (typeof simulation !== "undefined") {
            drawState(sliderVal);
        }
    }
})

$("input[name='mazeSize']").on("keyup", function () {
    var mazeSize = $(this).val();
    if (mazeSize) {
        editor.mazeSize = parseInt(mazeSize)
        editor.cellSize = canvasSize / editor.mazeSize;
        editor.mapArr = initializeMap(editor.mazeSize)
        editor.robot = {x: 1, y: 1, dirX: 1, dirY: 0}
        if (editor.editorMode) {
            reDrawEditor()
        }
    }
})

canvas.on("mousedown", function (e) {
    if (editor.editorMode) {
        editor.painting ^= 1

        var cellSize = canvasSize / editor.mazeSize
        editor.cellSize = cellSize;

        var coord = getMousePosition(canvas.get(0), e);

        var cellX = coord.x / cellSize | 0
        var cellY = coord.y / cellSize | 0
        if (editor.robot.x == cellX && editor.robot.y == cellY) {
            editor.brush = PLAYER
            editor.mapArr[xyToI(cellX, cellY, editor.mazeSize)] = CLEAR
        } else {
            var selectedBlock = editor.mapArr[xyToI(cellX, cellY, editor.mazeSize)]
            if (selectedBlock == WALL) {
                editor.brush = CLEAR
            } else if (selectedBlock == CLEAR) {
                editor.brush = WALL
            }

        }
        reDrawEditor()
    }
});

canvas.on("mousemove", function (e) {
    if (editor.editorMode && editor.painting) {
        var cellSize = editor.cellSize;
        var coord = getMousePosition(canvas.get(0), e);

        var cellX = coord.x / cellSize | 0
        var cellY = coord.y / cellSize | 0

        if (editor.brush != PLAYER) {
            editor.mapArr[xyToI(cellX, cellY, editor.mazeSize)] = editor.brush;
        } else {
            editor.robot.x = cellX;
            editor.robot.y = cellY;
        }
        reDrawEditor()
    }
})


$("#resetButton").on("click", function () {
    animationMs = getAnimationMS();
    sliderVal = 0;
    slider[0].value = sliderVal;
    if (isSimulationPresent()) {
        drawState(sliderVal);
    }
})
$("#stopButton").on("click", function () {
    animationMs = getAnimationMS();
    isAnimation = false;
})

function disableEditorMode() {
    editor.editorMode = false;
    $("input[name='editorModeCheckBox']").prop("checked", false)
}

$("#playButton").on("click", function () {
    disableEditorMode();
    animationMs = getAnimationMS();
    if (isSimulationPresent()) {
        isAnimation = true;
        var adjustFnct = function adjustSlider() {
            if (!isAnimation) {
                return
            }
            var currentVal = parseInt(slider[0].value);
            if (currentVal + 1 <= (simulation.states.length - 1)) {
                sliderVal = currentVal + 1;
                slider[0].value = sliderVal;
                drawState(sliderVal);
                setTimeout(adjustFnct, animationMs);
            }
        }
        setTimeout(adjustFnct, animationMs);
    }
})

function isEditorChecked() {
    return $("input[name='editorModeCheckBox']").is(":checked")
}