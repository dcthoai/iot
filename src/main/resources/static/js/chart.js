const temperatureChartElement = document.getElementById('temperatureChart').getContext('2d');
const humidityChartElement = document.getElementById('humidityChart').getContext('2d');
const selectElement = document.getElementById('timePeriod');

function getChartOptions(chartType, chartLabel, dataLabel, chartData, background, border) {
    return {
        data: {
            labels: dataLabel,
            datasets: [
                {
                    type: chartType,
                    label: chartLabel,
                    data: chartData,
                    backgroundColor: background,
                    borderColor: border,
                    borderWidth: chartType === 'line' ? 4 : 1,
                    pointRadius: 6,
                    pointHoverRadius: 8,
                    fill: false,
                    z: 1
                }
            ]
        },
        options: {
            scales: {
                y: {
                    type: 'linear',
                    position: 'left',
                    beginAtZero: true
                },
                x: {
                    title: {
                        display: true,
                        text: 'Thời gian'
                    }
                }
            }
        }
    };
}

function getPreviousTimesByUnit(interval, count, unit) {
    const timesArray = ['Hiện tại'];
    const now = new Date();

    for (let i = 1; i <= count; i++) {
        if (i % 2 !== 0 && unit !== 'week') {
            timesArray.push('');
            continue;
        }

        const time = new Date(now);

        if (unit === 'hour') {
            time.setMinutes(now.getMinutes() - (i * interval));
        } else if (unit === 'day') {
            time.setHours(now.getHours() - (i * interval));
        } else {
            time.setDate(now.getDate() - (i * interval));
        }

        if (unit === 'hour' || unit === 'day') {
            const hours = time.getHours().toString().padStart(2, '0');
            const minutes = time.getMinutes().toString().padStart(2, '0');

            timesArray.push(`${hours}:${minutes}`);
        } else {
            const month = (time.getMonth() + 1).toString().padStart(2, '0');
            const day = time.getDate().toString().padStart(2, '0');

            timesArray.push(`${month}-${day}`);
        }
    }

    return timesArray.reverse();
}

function getMonthsAndDays(year) {
    const months = {
        "1": 31,
        "2": (year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0)) ? 29 : 28,
        "3": 31,
        "4": 30,
        "5": 31,
        "6": 30,
        "7": 31,
        "8": 31,
        "9": 30,
        "10": 31,
        "11": 30,
        "12": 31
    };

    for (let month in months) {
        const daysInMonth = months[month];
        months[month] = Array.from({ length: daysInMonth }, (_, i) => i + 1);
    }

    return months;
}

async function getDataStatistic(type) {
    const url = BASE_URL + `api/statistic?type=${type}`;
    let response = await fetch(url);

    if (response.ok) {
        response = await response.json();
        return response;
    } else {
        return null;
    }
}

let temperatureChart;
let humidityChart;
let temperatureChartOptions = {};
let humidityChartOptions = {};

const avgTemperature = document.getElementById('avgTemperature');
const avgHumidity = document.getElementById('avgHumidity');
const maxTemperature = document.getElementById('maxTemperature');
const minTemperature = document.getElementById('minTemperature');
const maxHumidity = document.getElementById('maxHumidity');
const minHumidity = document.getElementById('minHumidity');
const firstTimeAnalysis = document.getElementById('firstTimeAnalysis');
const lastTimeAnalysis = document.getElementById('lastTimeAnalysis');
let refreshAnalyzeDataInterval;

// Function to fetch analysis data from the API
async function fetchAnalysisData(type) {
    try {
        const response = await fetch(BASE_URL + `api/statistic/analyze?type=${type}`);

        if (response.ok) {
            const analysisResult = await response.json();

            avgTemperature.innerHTML = analysisResult.avgTemperature + ' °C';
            avgHumidity.innerHTML = analysisResult.avgHumidity + ' %';
            maxTemperature.innerHTML = analysisResult.maxTemperature + ' °C';
            minTemperature.innerHTML = analysisResult.minTemperature + ' °C';
            maxHumidity.innerHTML = analysisResult.maxHumidity + ' %';
            minHumidity.innerHTML = analysisResult.minHumidity + ' %';
            firstTimeAnalysis.innerHTML = analysisResult.firstTimeAnalysis;
            lastTimeAnalysis.innerHTML = analysisResult.lastTimeAnalysis;
        } else {
            avgTemperature.innerHTML = 'Không có dữ liệu';
            avgHumidity.innerHTML = 'Không có dữ liệu';
            maxTemperature.innerHTML = 'Không có dữ liệu';
            minTemperature.innerHTML = 'Không có dữ liệu';
            maxHumidity.innerHTML = 'Không có dữ liệu';
            minHumidity.innerHTML = 'Không có dữ liệu';
            firstTimeAnalysis.innerHTML = 'Không có dữ liệu';
            lastTimeAnalysis.innerHTML = 'Không có dữ liệu';
            console.error('Failed to fetch data from the server.');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function updateChart(type) {
    const response = await getDataStatistic(type);

    if (response && response.success) {
        const temperatures = response.data.map(entry => entry.temperature);
        const humidity = response.data.map(entry => entry.humidity);

        temperatureChartOptions.data.datasets[0].data = temperatures;
        humidityChartOptions.data.datasets[0].data = humidity;

        temperatureChart.update();
        humidityChart.update();
    }
}

window.addEventListener('DOMContentLoaded', async () => {
    const response = await getDataStatistic(1);

    if (response && response.success) {
        const temperatures = response.data.map(entry => entry.temperature);
        const humidity = response.data.map(entry => entry.humidity);
        const labels = getPreviousTimesByUnit(5, 12, 'hour');

        // Auto update data of chart
        refreshAnalyzeDataInterval = setInterval(() => {
            fetchAnalysisData(1);
            updateChart(1);
        }, timeRefreshDataInterval);

        temperatureChartOptions = getChartOptions(
            'line',
            'Nhiệt độ (°C)',
            labels,
            temperatures,
            'rgb(255, 13, 13)',
            'rgb(255, 13, 13)'
        );

        humidityChartOptions = getChartOptions(
            'bar',
            'Độ ẩm (%)',
            labels,
            humidity,
            'rgba(0, 135, 206, 0.5)',
            'rgb(0, 135, 206)'
        );

        temperatureChart = new Chart(temperatureChartElement, temperatureChartOptions);
        humidityChart = new Chart(humidityChartElement, humidityChartOptions);
    } else {
        console.error("Failed to fetch initial data for charts.");
    }
});

selectElement.addEventListener('change', async function() {
    const selectedValue = this.value;
    const response = await getDataStatistic(selectedValue);

    // Auto update data of chart
    clearInterval(refreshAnalyzeDataInterval);
    refreshAnalyzeDataInterval = setInterval(() => {
        fetchAnalysisData(selectedValue);
        updateChart(selectedValue);
    }, timeRefreshDataInterval);

    if (response && response.success) {
        const temperatures = response.data.map(entry => entry.temperature);
        const humidity = response.data.map(entry => entry.humidity);

        temperatureChartOptions.data.datasets[0].data = temperatures;
        humidityChartOptions.data.datasets[0].data = humidity;

        if (selectedValue === '1') {
            const hourLabels = getPreviousTimesByUnit(5, 12, 'hour');
            humidityChartOptions.data.labels = hourLabels;
            temperatureChartOptions.data.labels = hourLabels;
        } else if (selectedValue === '2') {
            const dayLabels = getPreviousTimesByUnit(2, 12, 'day');
            humidityChartOptions.data.labels = dayLabels;
            temperatureChartOptions.data.labels = dayLabels;
        } else if (selectedValue === '3') {
            const weekLabels = getPreviousTimesByUnit(1, 6, 'week');
            humidityChartOptions.data.labels = weekLabels;
            temperatureChartOptions.data.labels = weekLabels;
        } else if (selectedValue === '4') {
            const currentDate = new Date();
            const currentYear = currentDate.getFullYear();
            const currentMonth = currentDate.getMonth() + 1;
            const monthDays = getMonthsAndDays(currentYear)[currentMonth];
            const monthLabels = getPreviousTimesByUnit(1, monthDays.length - 1, 'month');

            humidityChartOptions.data.labels = monthLabels;
            temperatureChartOptions.data.labels = monthLabels;
        }

        temperatureChart.update();
        humidityChart.update();
        console.log(`Updated charts with ${selectedValue} data.`);
    } else {
        openPopupNotify('Thất bại', '', 'error');
        console.error("Failed to fetch data or update charts.");
    }
});

