package com.example.motionauth.Processing;

import android.util.Log;
import com.example.motionauth.Utility.Enum;
import com.example.motionauth.Utility.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;


/**
 * データの時間的なズレを修正する
 *
 * @author Kensuke Kousaka
 */
public class CorrectDeviation {

	/**
	 * 取得回数ごとのデータのズレを時間的なズレを修正する
	 *
	 * @param distance 修正する距離データ
	 * @param angle 修正する角度データ
	 * @param mode どこを基準にとるか
	 * @param target どちらのデータに基準を置くか
	 * @return newData ズレ修正後のdouble型の4次元配列データ
	 */
	public double[][][][] correctDeviation (double[][][] distance, double[][][] angle, Enum.MODE mode, Enum.TARGET target) {
		LogUtil.log(Log.INFO);

		// ずらしたデータを格納する配列
		double[][][][] newData = new double[2][3][3][100];

		// 試行回ごとの代表値の出ている時間を抽出
		// 変数は，桁揃え，計算後のdistance，angleを利用する

		// 回数・XYZを配列で
		double tmpValue[][] = new double[3][3];

		// 代表値の出ている時間，回数，XYZ
		int count[][] = new int[3][3];

		// 変数に3回分XYZそれぞれの1個目の値を放り込む
		switch (target) {
			case DISTANCE:
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						tmpValue[i][j] = distance[i][j][0];
					}
				}
				break;
			case ANGLE:
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						tmpValue[i][j] = angle[i][j][0];
					}
				}
				break;
		}

		// 代表値が出ている場所を取得する
		switch (target) {
			case DISTANCE:
				switch (mode) {
					case MAX:
						for (int i = 0; i < 3; i++) {
							for (int j = 0; j < 3; j++) {
								for (int k = 0; k < 100; k++) {
									if (tmpValue[i][j] < distance[i][j][k]) {
										tmpValue[i][j] = distance[i][j][k];
										count[i][j] = k;
									}
								}
							}
						}
						break;
					case MIN:
						for (int i = 0; i < 3; i++) {
							for (int j = 0; j < 3; j++) {
								for (int k = 0; k < 100; k++) {
									if (tmpValue[i][j] > distance[i][j][k]) {
										tmpValue[i][j] = distance[i][j][k];
										count[i][j] = k;
									}
								}
							}
						}
						break;
					case MEDIAN:
						// キーが自動ソートされるTreeMapを用いる．
                        // データと順番を紐付けしたものを作成し，中央値の初期の順番の値を取り出す．
						for (int i = 0; i < 3; i++) {
							for (int j = 0; j < 3; j++) {
								TreeMap<Double, Integer> treeMap = new TreeMap<>();

								for (int k = 0; k < 100; k++) {
									treeMap.put(distance[i][j][k], k);
								}

								int loopCount = 0;
								for (Integer initCount : treeMap.values()) {
									if (loopCount == 49) {
										count[i][j] = initCount;
									}

									loopCount++;
								}
							}
						}
						break;
				}
				break;
			case ANGLE:
				switch (mode) {
					case MAX:
						for (int i = 0; i < 3; i++) {
							for (int j = 0; j < 3; j++) {
								for (int k = 0; k < 100; k++) {
									if (tmpValue[i][j] < angle[i][j][k]) {
										tmpValue[i][j] = angle[i][j][k];
										count[i][j] = k;
									}
								}
							}
						}
						break;
					case MIN:
						for (int i = 0; i < 3; i++) {
							for (int j = 0; j < 3; j++) {
								for (int k = 0; k < 100; k++) {
									if (tmpValue[i][j] > angle[i][j][k]) {
										tmpValue[i][j] = angle[i][j][k];
										count[i][j] = k;
									}
								}
							}
						}
						break;
					case MEDIAN:
						// キーが自動ソートされるTreeMapを用いる．
                        // データと順番を紐付けしたものを作成し，中央値の初期の順番の値を取り出す．
						for (int i = 0; i < 3; i++) {
							for (int j = 0; j < 3; j++) {
								TreeMap<Double, Integer> treeMap = new TreeMap<>();

								for (int k = 0; k < 100; k++) {
									treeMap.put(angle[i][j][k], k);
								}

								int loopCount = 0;
								for (Integer initCount : treeMap.values()) {
									if (loopCount == 49) {
										count[i][j] = initCount;
									}

									loopCount++;
								}
							}
						}
						break;
				}
				break;
		}

		// 1回目のデータの代表値が出た場所と，2回目・3回目のデータの代表値が出た場所の差を取る
		// 取ったら，その差だけデータをずらす（ずらしてはみ出たデータは空いたとこに入れる）

		// ずらす移動量を計算
		int lagData[][] = new int[2][3];

		// どれだけズレているかを計算する
		for (int i = 0; i < 3; i++) {
			lagData[0][i] = count[0][i] - count[1][i];
			LogUtil.log(Log.DEBUG, "lagData[0]" + "[" + i + "]" + ": " + lagData[0][i]);

			lagData[1][i] = count[0][i] - count[2][i];
			LogUtil.log(Log.DEBUG, "lagData[1]" + "[" + i + "]" + ": " + lagData[1][i]);
		}

		// 1回目のデータに関しては基準となるデータなのでそのまま入れる
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 100; j++) {
				newData[0][0][i][j] = distance[0][i][j];
				newData[1][0][i][j] = angle[0][i][j];
			}
		}

		// 実際にデータをずらしていく（ずらすのは，1回目を除くデータ）
		for (int i = 1; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				ArrayList<Double> distanceTemp = new ArrayList<>();
				ArrayList<Double> angleTemp = new ArrayList<>();

				for (int k = 0; k < 100; k++) {
					distanceTemp.add(distance[i][j][k]);
					angleTemp.add(angle[i][j][k]);
				}
				Collections.rotate(distanceTemp, lagData[i - 1][j]);
				Collections.rotate(angleTemp, lagData[i - 1][j]);
				for (int k = 0; k < 100; k++) {
					newData[0][i][j][k] = distanceTemp.get(k);
					newData[1][i][j][k] = angleTemp.get(k);
				}
			}
		}

		return newData;
	}
}
