var WALL = -1;
var CLEAR = 0;
var PLAYER = -100

var canvasSize = 600;
var slider = $("#slider");
var fieldSize;
var sliderVal = slider.val();
var canvas = $("#myCanvas");
var strokeWidthPixel = 2;
canvas.attr("width", canvasSize)
canvas.attr("height", canvasSize)
var ctx = canvas[0].getContext("2d");
var darkColor = "#61380a";
var lightColor = "#dfd4a8";
var robotColor = "#FF0000";
var threatColor = "#FF0000";
var foodColor = "#ff66cc";
var sightDefaultColor = "#48CDEF";
var sightSeesColor = "#48EF69";
var seesAlpha = 0.62
var objectsAhead = {0: "empty", 1: "wall"};
var simulation;
var fitnessChartCtx = document.getElementById('fitnessChart').getContext('2d');
var fitnessChart
var fitnessChartGroupping = 10
var animationMs = getAnimationMS()
var isAnimation = false;
var fitnessDataFetchMs = 80
var genLimit = 0;
var simulationPhenotypes
var bestChromosomeInput = $("input[name='bestChromosome']")
var simulateChromosomeInput = $("input[name='simulateChromosome']")

var threatMap = initializeMap(getMazeSize())
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
    if (typeof fitnessChart !== 'undefined') {
        cleanChart(fitnessChart)
    } else {
        initializeFitnessChart([], []);
    }
    var fitnessChartData = fitnessChart.data.datasets[0].data
    var fitnessChartLabels = fitnessChart.data.labels
    var getSimulationsFnct = function getSimulations() {
        $.ajax({
            contentType: 'application/json',
            dataType: 'json',
            success: function (phenotype) {
                if (!phenotype.finished) {
                    setTimeout(getSimulationsFnct, fitnessDataFetchMs);
                }
                if (phenotype.fitness != -1) {
                    var mazeSize1 = getMazeSize();
                    editor.mapArr = initializeMap(mazeSize1);
                    for (var i = 0; i < phenotype.genes.length; i++) {
                        editor.mapArr[xyToI(i, phenotype.genes[i] - 1, mazeSize1)] = -1;
                    }

                    reDrawEditor()
                    fitnessChartData.push(phenotype.fitness)
                    fitnessChartLabels.push("#" + phenotype.generation + " gen")
                    fitnessChart.update();
                }
            },
            type: 'GET',
            url: "http://localhost:8080/queen/getPhenotype"
        })
    }
    setTimeout(getSimulationsFnct, fitnessDataFetchMs);


    // disableEditorMode();
    genLimit = $("input[name='genLimit']").val();
    var mazeSize = getMazeSize();


    var req = {
        editorConfig: {
            mazeSize: getMazeSize(),
        },
        simulatorConfig: {
            genLimit: genLimit,
        },
    }

    var url = "http://localhost:8080/queen/evolve"
    $.ajax({
        contentType: 'application/json',
        data: JSON.stringify(req),
        dataType: 'json',
        success: function (simulationResponse) {
            // simulation = simulationResponse
            // sliderVal = 0
            // slider.val(sliderVal)
            // console.log("done..")
            //
            // slider.attr("max", simulation.states.length - 1)
            // drawState(sliderVal)
            // slider.on("input change", function () {
            //     sliderVal = slider.val();
            //     drawState(sliderVal);
            // })
        },
        type: 'POST',
        url: url
    })
})

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
        var coord = toXY(i, mazeSize);
        var parity = (coord.x + coord.y) % 2
        fillRect(coord, parity == 0 ? darkColor : lightColor, 1.0, cellSize)
    }
    threatMap = initializeMap(mazeSize)
    //check row
    for (var row = 0; row < mazeSize; row++) {
        var nQ = 0
        for (var col = 0; col < mazeSize; col++) {
            var i = xyToI(col, row, mazeSize);
            if (mapArr[i] == -1) {
                nQ++;
            }
        }
        if (nQ != 0) {
            for (var col = 0; col < mazeSize; col++) {
                var i = xyToI(col, row, mazeSize);
                if (threatMap[i] < 2) {
                    threatMap[i] = nQ
                }
            }
        }
    }
    //check column
    for (var col = 0; col < mazeSize; col++) {
        var nQ = 0
        for (var row = 0; row < mazeSize; row++) {
            var i = xyToI(col, row, mazeSize);
            if (mapArr[i] == -1) {
                nQ++;
            }
        }
        if (nQ != 0) {
            for (var row = 0; row < mazeSize; row++) {
                var i = xyToI(col, row, mazeSize);
                if (threatMap[i] < 2) {
                    threatMap[i] = nQ
                }
            }
        }
    }
    //check right-down diagonal
    for (var col = 0; col < mazeSize; col++) {
        var nQ = 0
        for (var row = 0; row < mazeSize; row++) {
            if (col + row < mazeSize) {
                var i = xyToI(col + row, row, mazeSize);
                if (mapArr[i] == -1) {
                    nQ++;
                }
            }
        }
        if (nQ != 0) {
            for (var row = 0; row < mazeSize; row++) {
                if (col + row < mazeSize) {
                    var i = xyToI(col + row, row, mazeSize);
                    if (threatMap[i] < 2) {
                        threatMap[i] = nQ
                    }

                }
            }
        }
    }

    //check right-down diagonal
    for (var row = 0; row < mazeSize; row++) {
        var nQ = 0
        for (var cc = 0; cc < mazeSize; cc++) {
            if (row + cc < mazeSize) {
                var i = xyToI(cc, row + cc, mazeSize);
                if (mapArr[i] == -1) {
                    nQ++;
                }
            }
        }
        if (nQ != 0) {
            for (var cc = 0; cc < mazeSize; cc++) {
                if (row + cc < mazeSize) {
                    var i = xyToI(cc, row + cc, mazeSize);
                    if (threatMap[i] < 2) {
                        threatMap[i] = nQ
                    }

                }
            }
        }
    }

    //check right-up diagonal
    for (var row = 0; row < mazeSize; row++) {
        var nQ = 0
        for (var cc = 0; cc < mazeSize; cc++) {
            if (row - cc >= 0) {
                var i = xyToI(cc, row - cc, mazeSize);
                if (mapArr[i] == -1) {
                    nQ++;
                }
            }
        }
        if (nQ != 0) {
            for (var cc = 0; cc < mazeSize; cc++) {
                if (row - cc >= 0) {
                    var i = xyToI(cc, row - cc, mazeSize);
                    if (threatMap[i] < 2) {
                        threatMap[i] = nQ
                    }

                }
            }
        }
    }


    for (var col = 0; col < mazeSize; col++) {
        var nQ = 0
        for (var row = 0; row < mazeSize; row++) {
            if (mazeSize - 1 - row >= 0 && col + row < mazeSize) {
                var i = xyToI(col + row, mazeSize - 1 - row, mazeSize);
                if (mapArr[i] == -1) {
                    nQ++;
                }
            }
        }
        if (nQ != 0) {
            for (var row = 0; row < mazeSize; row++) {
                if (mazeSize - 1 - row >= 0 && col + row < mazeSize) {
                    var i = xyToI(col + row, mazeSize - 1 - row, mazeSize);
                    if (threatMap[i] < 2) {
                        threatMap[i] = nQ
                    }
                }
            }
        }
    }


    for (var i = 0; i < threatMap.length; i++) {
        var block = threatMap[i]
        var coord = toXY(i, mazeSize);
        if (block > 1) {
            fillRect(coord, threatColor, 0.6, cellSize)
        }
        if (block == 1) {
            fillRect(coord, sightSeesColor, 0.6, cellSize)
        }
    }
    for (var i = 0; i < mapArr.length; i++) {
        var block = mapArr[i]
        var coord = toXY(i, mazeSize);
        if (block == -1) {
            var img = $("#queen").get(0)
            var scaleFactor = 0.7
            ctx.drawImage(img, coord.x * cellSize + cellSize * (1 - scaleFactor) / 2, coord.y * cellSize + cellSize * (1 - scaleFactor) / 2, cellSize * scaleFactor, cellSize * scaleFactor)

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
        threatMap = initializeMap(editor.mazeSize)
        editor.robot = {x: 1, y: 1, dirX: 1, dirY: 0}
        if (editor.editorMode) {
            reDrawEditor()
        }
    }
})

canvas.on("mousedown", function (e) {
    if (editor.editorMode) {

        var cellSize = canvasSize / editor.mazeSize
        editor.cellSize = cellSize;

        var coord = getMousePosition(canvas.get(0), e);

        var cellX = coord.x / cellSize | 0
        var cellY = coord.y / cellSize | 0

        var block = editor.mapArr[xyToI(cellX, cellY, editor.mazeSize)]

        if (block == 0) {
            editor.mapArr[xyToI(cellX, cellY, editor.mazeSize)] = -1
        } else if (block == -1) {
            editor.mapArr[xyToI(cellX, cellY, editor.mazeSize)] = 0;
        }
        reDrawEditor()
    }
});

$("#clearButton").on("click", function () {
    editor.mapArr = initializeMap(getMazeSize())
    if (editor.editorMode) {
        reDrawEditor()
        cleanChart(fitnessChart)
        fitnessChart.update();
    }
})

$("#randomizeButton").on("click", function () {
    var mazeSize = getMazeSize();
    editor.mapArr = initializeMap(mazeSize)
    if (editor.editorMode) {
        for (var i = 0; i < mazeSize; i++) {
            var j = Math.round(Math.random() * (mazeSize - 1));
            editor.mapArr[xyToI(i, j, mazeSize)] = -1;
        }
        reDrawEditor()
    }
})

$("#copyGenes").on("click", function () {
    simulateChromosomeInput.val(bestChromosomeInput.val())
})
$("#randomGene").on("click", function () {
    var arr = [];
    for (var i = 0, t = 4096; i < t; i++) {
        arr.push(Math.round(Math.random()))
    }
    simulateChromosomeInput.val(arr)
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

function initializeFitnessChart(labels, chartData) {
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

function cleanChart(chart) {
    if (typeof chart !== 'undefined') {
        chart.data.labels.pop()
        chart.data.labels = [];
        chart.data.datasets[0].data.pop()
        chart.data.datasets[0].data = [];
        chart.update();
    }
}