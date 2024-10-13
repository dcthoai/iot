const temperatureChartElement = document.getElementById('temperatureChart').getContext('2d');
const humidityChartElement = document.getElementById('humidityChart').getContext('2d');
const selectElement = document.getElementById('timePeriod');
const chartDataLabels = {
    hour: ['0', '5p', '10p', '15p', '20p', '25p', '30p', '35p', '40p', '45p', '50p', '55p', '1h'],
    day: ['0h', '2h', '4h', '6h', '8h', '10h', '12h', '14h', '16h', '18h', '20h', '22h', '24h'],
    week: ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'CN']
}

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

window.addEventListener('DOMContentLoaded', async () => {
    const response = await getDataStatistic(1);

    if (response && response.success) {
        const temperatures = response.data.map(entry => entry.temperature);
        const humidity = response.data.map(entry => entry.humidity);

        temperatureChartOptions = getChartOptions(
            'line',
            'Nhiệt độ (°C)',
            chartDataLabels.hour,
            temperatures,
            'rgb(255, 13, 13)',
            'rgb(255, 13, 13)'
        );

        humidityChartOptions = getChartOptions(
            'bar',
            'Độ ẩm (%)',
            chartDataLabels.hour,
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

    if (response && response.success) {
        const temperatures = response.data.map(entry => entry.temperature);
        const humidity = response.data.map(entry => entry.humidity);

        temperatureChartOptions.data.datasets[0].data = temperatures;
        humidityChartOptions.data.datasets[0].data = humidity;

        if (selectedValue == 1) {
            humidityChartOptions.data.labels = chartDataLabels.hour;
            temperatureChartOptions.data.labels = chartDataLabels.hour;
        } else if (selectedValue == 2) {
            humidityChartOptions.data.labels = chartDataLabels.day;
            temperatureChartOptions.data.labels = chartDataLabels.day;
        } else if (selectedValue == 3) {
            humidityChartOptions.data.labels = chartDataLabels.week;
            temperatureChartOptions.data.labels = chartDataLabels.week;
        } else if (selectedValue == 4) {
            const currentDate = new Date();
            const currentYear = currentDate.getFullYear();
            const currentMonth = currentDate.getMonth() + 1;
            humidityChartOptions.data.labels = getMonthsAndDays(currentYear)[currentMonth];
            temperatureChartOptions.data.labels = getMonthsAndDays(currentYear)[currentMonth];
        }

        temperatureChart.update();
        humidityChart.update();
        console.log(`Updated charts with ${selectedValue} data.`);
    } else {
        console.error("Failed to fetch data or update charts.");
    }
});

