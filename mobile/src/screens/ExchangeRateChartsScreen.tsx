import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  Dimensions,
} from 'react-native';
// import { LineChart } from 'react-native-chart-kit'; // USUNIĘTE - problemy z SVG
import { useQuery } from '@tanstack/react-query';
import { exchangeRateService } from '@/services/exchangeRateService';

const { width } = Dimensions.get('window');

type TimeRange = '7d' | '30d' | '90d';
type Currency = 'USD' | 'EUR' | 'GBP' | 'CHF';

const ExchangeRateChartsScreen = () => {
  const [selectedCurrency, setSelectedCurrency] = useState<Currency>('USD');
  const [timeRange, setTimeRange] = useState<TimeRange>('30d');

  const { data: rates, isLoading } = useQuery({
    queryKey: ['exchangeRates', selectedCurrency, timeRange],
    queryFn: () => exchangeRateService.getHistoricalRates(selectedCurrency, timeRange),
  });

  const currencies: Currency[] = ['USD', 'EUR', 'GBP', 'CHF'];
  const timeRanges: { value: TimeRange; label: string }[] = [
    { value: '7d', label: '7 dni' },
    { value: '30d', label: '30 dni' },
    { value: '90d', label: '90 dni' },
  ];

  const currencyNames: Record<Currency, string> = {
    USD: 'Dolar (USD)',
    EUR: 'Euro (EUR)',
    GBP: 'Funt (GBP)',
    CHF: 'Frank (CHF)',
  };

  const getChartData = () => {
    if (!rates || rates.length === 0) {
      return {
        labels: [],
        datasets: [{ data: [] }],
      };
    }

    // Ogranicz liczbę etykiet dla czytelności
    const maxLabels = 7;
    const step = Math.ceil(rates.length / maxLabels);
    
    const labels = rates
      .filter((_, index) => index % step === 0)
      .map((item) => {
        const date = new Date(item.effectiveDate);
        return `${date.getDate()}/${date.getMonth() + 1}`;
      });

    const data = rates.map((item) => item.rate);

    return {
      labels,
      datasets: [
        {
          data,
          color: (opacity = 1) => `rgba(0, 122, 255, ${opacity})`,
          strokeWidth: 2,
        },
      ],
    };
  };

  const getStatistics = () => {
    if (!rates || rates.length === 0) return null;

    const values = rates.map((r) => r.rate);
    const min = Math.min(...values);
    const max = Math.max(...values);
    const avg = values.reduce((a, b) => a + b, 0) / values.length;
    const current = values[values.length - 1];
    const previous = values[0];
    const change = ((current - previous) / previous) * 100;

    return { min, max, avg, current, change };
  };

  const stats = getStatistics();

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Wykresy Kursów Walut</Text>
        <Text style={styles.subtitle}>Analiza historyczna z NBP</Text>
      </View>

      {/* Currency Selector */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Wybierz walutę:</Text>
        <View style={styles.buttonRow}>
          {currencies.map((currency) => (
            <TouchableOpacity
              key={currency}
              style={[
                styles.currencyButton,
                selectedCurrency === currency && styles.currencyButtonActive,
              ]}
              onPress={() => setSelectedCurrency(currency)}
            >
              <Text
                style={[
                  styles.currencyButtonText,
                  selectedCurrency === currency && styles.currencyButtonTextActive,
                ]}
              >
                {currency}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {/* Time Range Selector */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Okres czasu:</Text>
        <View style={styles.buttonRow}>
          {timeRanges.map((range) => (
            <TouchableOpacity
              key={range.value}
              style={[
                styles.timeButton,
                timeRange === range.value && styles.timeButtonActive,
              ]}
              onPress={() => setTimeRange(range.value)}
            >
              <Text
                style={[
                  styles.timeButtonText,
                  timeRange === range.value && styles.timeButtonTextActive,
                ]}
              >
                {range.label}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {/* Chart */}
      {isLoading ? (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#007AFF" />
          <Text style={styles.loadingText}>Ładowanie danych...</Text>
        </View>
      ) : rates && rates.length > 0 ? (
        <>
          <View style={styles.chartContainer}>
            <Text style={styles.chartTitle}>
              {currencyNames[selectedCurrency]} / PLN
            </Text>
            
            {/* PROSTY WYKRES TEKSTOWY - zamiast LineChart */}
            <View style={styles.simpleChart}>
              <Text style={styles.chartNote}>
                📊 Dane historyczne dla {currencyNames[selectedCurrency]}
              </Text>
              <Text style={styles.chartNote}>
                📅 Okres: {timeRanges.find(r => r.value === timeRange)?.label}
              </Text>
              {rates && rates.length > 0 && (
                <View style={styles.dataList}>
                  {rates.slice(0, 5).map((rate, idx) => (
                    <View key={idx} style={styles.dataRow}>
                      <Text style={styles.dataDate}>{rate.effectiveDate}</Text>
                      <Text style={styles.dataRate}>{rate.rate.toFixed(4)} PLN</Text>
                    </View>
                  ))}
                  {rates.length > 5 && (
                    <Text style={styles.moreData}>
                      ... i {rates.length - 5} więcej punktów danych
                    </Text>
                  )}
                </View>
              )}
            </View>
          </View>

          {/* Statistics */}
          {stats && (
            <View style={styles.statsContainer}>
              <Text style={styles.statsTitle}>Statystyki</Text>

              <View style={styles.statsGrid}>
                <View style={styles.statBox}>
                  <Text style={styles.statLabel}>Aktualny</Text>
                  <Text style={styles.statValue}>{stats.current.toFixed(4)}</Text>
                </View>

                <View style={styles.statBox}>
                  <Text style={styles.statLabel}>Zmiana</Text>
                  <Text
                    style={[
                      styles.statValue,
                      stats.change > 0 ? styles.statPositive : styles.statNegative,
                    ]}
                  >
                    {stats.change > 0 ? '+' : ''}
                    {stats.change.toFixed(2)}%
                  </Text>
                </View>

                <View style={styles.statBox}>
                  <Text style={styles.statLabel}>Minimum</Text>
                  <Text style={styles.statValue}>{stats.min.toFixed(4)}</Text>
                </View>

                <View style={styles.statBox}>
                  <Text style={styles.statLabel}>Maximum</Text>
                  <Text style={styles.statValue}>{stats.max.toFixed(4)}</Text>
                </View>

                <View style={styles.statBox}>
                  <Text style={styles.statLabel}>Średnia</Text>
                  <Text style={styles.statValue}>{stats.avg.toFixed(4)}</Text>
                </View>

                <View style={styles.statBox}>
                  <Text style={styles.statLabel}>Próbki</Text>
                  <Text style={styles.statValue}>{rates.length}</Text>
                </View>
              </View>
            </View>
          )}

          {/* Info */}
          <View style={styles.infoContainer}>
            <Text style={styles.infoText}>
              ℹ️ Kursy pobierane z API Narodowego Banku Polskiego (NBP)
            </Text>
            <Text style={styles.infoText}>
              📊 Dane aktualizowane codziennie w dni robocze
            </Text>
          </View>
        </>
      ) : (
        <View style={styles.emptyContainer}>
          <Text style={styles.emptyText}>Brak danych dla wybranego okresu</Text>
        </View>
      )}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f7fb',
  },
  header: {
    backgroundColor: '#fff',
    padding: 20,
    paddingTop: 50,
    borderBottomWidth: 1,
    borderBottomColor: '#e5e5ea',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#000',
  },
  subtitle: {
    fontSize: 14,
    color: '#666',
    marginTop: 4,
  },
  section: {
    padding: 20,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 12,
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  currencyButton: {
    flex: 1,
    backgroundColor: '#fff',
    padding: 12,
    marginHorizontal: 4,
    borderRadius: 8,
    alignItems: 'center',
    borderWidth: 2,
    borderColor: '#ddd',
  },
  currencyButtonActive: {
    backgroundColor: '#007AFF',
    borderColor: '#007AFF',
  },
  currencyButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
  },
  currencyButtonTextActive: {
    color: '#fff',
  },
  timeButton: {
    flex: 1,
    backgroundColor: '#fff',
    padding: 12,
    marginHorizontal: 4,
    borderRadius: 8,
    alignItems: 'center',
    borderWidth: 2,
    borderColor: '#ddd',
  },
  timeButtonActive: {
    backgroundColor: '#28a745',
    borderColor: '#28a745',
  },
  timeButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
  },
  timeButtonTextActive: {
    color: '#fff',
  },
  loadingContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    padding: 40,
  },
  loadingText: {
    marginTop: 12,
    fontSize: 14,
    color: '#666',
  },
  chartContainer: {
    backgroundColor: '#fff',
    marginHorizontal: 20,
    marginBottom: 20,
    padding: 16,
    borderRadius: 12,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  chartTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 12,
    textAlign: 'center',
  },
  chart: {
    borderRadius: 16,
  },
  simpleChart: {
    padding: 16,
    backgroundColor: '#f5f7fb',
    borderRadius: 12,
  },
  chartNote: {
    fontSize: 14,
    color: '#666',
    marginBottom: 8,
  },
  dataList: {
    marginTop: 16,
  },
  dataRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#e5e5ea',
  },
  dataDate: {
    fontSize: 14,
    color: '#333',
  },
  dataRate: {
    fontSize: 14,
    fontWeight: '600',
    color: '#007AFF',
  },
  moreData: {
    fontSize: 12,
    color: '#999',
    marginTop: 8,
    textAlign: 'center',
  },
  statsContainer: {
    backgroundColor: '#fff',
    marginHorizontal: 20,
    marginBottom: 20,
    padding: 16,
    borderRadius: 12,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  statsTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 16,
  },
  statsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  statBox: {
    width: '48%',
    backgroundColor: '#f5f7fb',
    padding: 12,
    borderRadius: 8,
    marginBottom: 12,
  },
  statLabel: {
    fontSize: 12,
    color: '#666',
    marginBottom: 4,
  },
  statValue: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  statPositive: {
    color: '#28a745',
  },
  statNegative: {
    color: '#dc3545',
  },
  infoContainer: {
    backgroundColor: '#e3f2fd',
    marginHorizontal: 20,
    marginBottom: 20,
    padding: 16,
    borderRadius: 8,
  },
  infoText: {
    fontSize: 12,
    color: '#1976d2',
    marginBottom: 4,
  },
  emptyContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    padding: 40,
  },
  emptyText: {
    fontSize: 16,
    color: '#666',
  },
});

export default ExchangeRateChartsScreen;
