/*
Copyright © 2025 danieljhkim
*/
package cmd

import (
	"fmt"

	"github.com/spf13/cobra"
)

var pingCmd = &cobra.Command{
	Use:   "ping",
	Short: "Test connection to server",
	Args:  cobra.NoArgs,
	Run: func(cmd *cobra.Command, args []string) {
		response, err := kvClient.ExecuteCommand("KV PING")
		if err != nil {
			fmt.Printf("Error: %v\n", err)
			return
		}
		fmt.Println(response)
	},
}

func init() {
	rootCmd.AddCommand(pingCmd)
}
